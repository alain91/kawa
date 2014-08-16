// Copyright (c) 2014  Alain Gandon.
// Copyright (c) 1997, 2004, 2008  Per M.A. Bothner.
// This is free software;  for terms and warranty disclaimer see ./COPYING.

package gnu.bytecode;
import java.io.InputStream;
import java.io.DataInputStream;
import java.io.IOException;
import gnu.mapping.WrappedException;

/** Class to read a ClassType from a DataInputStream (.class file).
 * @author Per Bothner
 * @author (revised by) Alain Gandon
 */

public class ClassFileInput
{
  ClassType ctype;
  String cname;
  int magic;
  
  public ClassFileInput (String cname)
      throws IOException
  {
    this.cname = cname;
    this.ctype = new ClassType(); // new empty ClassType
  }
  
  public DataInputStream getDataInputStream (String name)
      throws IOException
  {
    DataInputStream dis = null;
    try
      {
        String entryName = name.replace('.','/')+".class";
        ClassLoader cLoader = ctype.getClass().getClassLoader();
        InputStream i = cLoader.getResourceAsStream(entryName);
        if (i == null) return null;
        return new DataInputStream(i);
      }
    catch (Exception ex)
      {
          throw new WrappedException(ex);
      }
  }
  
  public boolean isAvailable()
  {
    return (this.magic == 0xcafebabe);
  }
  
	/** Read file header for magic number
		* @return boolean false if magic NOK
		*/
  public boolean readHeader (DataInputStream dis)
      throws IOException
  {
    int magic = dis.readInt();
    if (magic != 0xcafebabe) return false;
    this.magic = magic;
    return true;
  }

  public void readVersion (DataInputStream dis)
      throws IOException
  {
    int minor = dis.readUnsignedShort();
    int major = dis.readUnsignedShort();
    ctype.classfileFormatVersion = (major * 0x10000 + minor);
  }

  public void readConstants (DataInputStream dis)
      throws IOException
  {
    ctype.constants = new ConstantPool();
    ctype.constants.readConstants(dis);
  }

  public void readClassInfo (DataInputStream dis)
      throws IOException
  {
    CpoolClass clas;
    String name;

    ctype.access_flags = dis.readUnsignedShort();

    ctype.thisClassIndex = dis.readUnsignedShort();
    clas = getClassConstant(ctype.thisClassIndex);
    name = clas.name.string;
    ctype.this_name = ctype.thisClassName = name.replace('/', '.');
    // ctype.setSignature("L"+name+";");

    ctype.superClassIndex = dis.readUnsignedShort();
    if (ctype.superClassIndex == 0)
  ctype.superClassName = "";
  // ctype.setSuper((ClassType) null);
    else
      {
	clas = getClassConstant(ctype.superClassIndex);
	name = clas.name.string;
  ctype.superClassName = name.replace('/', '.');
	// ctype.setSuper(name.replace('/', '.'));
      }
	}

	public void readInterfaces (DataInputStream dis)
      throws IOException
	{
    CpoolClass clas;
    String name;
    int nInterfaces = dis.readUnsignedShort();
    if (nInterfaces <= 0) return;

    ctype.interfacesName = new String[nInterfaces];
    ctype.interfaceIndexes = new int[nInterfaces];
    for (int i = 0;  i < nInterfaces;  i++)
      {
  int index = dis.readUnsignedShort();
  ctype.interfaceIndexes[i] = index;
  clas = getClassConstant(index);
  name = clas.name.string.replace('/', '.');
  // ctype.interfacesName[i] = name;
      }
  }
  
  public void readFields (DataInputStream dis)
      throws IOException
  {
    int nFields = dis.readUnsignedShort();
    if (nFields <= 0) return;
    
    for (int i = 0;  i < nFields;  i++)
      {
  int flags = dis.readUnsignedShort();
  int nameIndex = dis.readUnsignedShort();
  int descriptorIndex = dis.readUnsignedShort();
  // System.err.printf ("%s - fields %d - %d: 0x%x\n", cname, i, nFields, flags);
  Field fld = ctype.addField();
  fld.setName(nameIndex, ctype.constants);
  // fld.setSignature(descriptorIndex, ctype.constants);
  fld.signature_index = descriptorIndex;
  fld.flags = flags;
  readAttributes(dis, fld);
      }
  }

  public void readMethods (DataInputStream dis)
      throws IOException
  {
    int nMethods = dis.readUnsignedShort();
    if (nMethods <= 0) return;
    
    for (int i = 0;  i < nMethods;  i++)
      {
	int flags = dis.readUnsignedShort();
	int nameIndex = dis.readUnsignedShort();
	int descriptorIndex = dis.readUnsignedShort();
  // System.err.printf ("%s - methods %d - %d: 0x%x\n", cname, i, nMethods, flags);
	Method meth = ctype.addMethod(null, flags);
	meth.setName(nameIndex);
	// meth.setSignature(descriptorIndex);
  meth.signature_index = descriptorIndex;
	readAttributes(dis, meth);
      }
  }

  public void readAttributes (DataInputStream dis, AttrContainer container)
      throws IOException
  {
    int count = dis.readUnsignedShort();
    Attribute last = container.getAttributes();

    for (int i = 0;  i < count;  i++)
      {
	if (last != null)
	  {
	    for (;;)
	      {
		Attribute next = last.getNext();
		if (next == null)
		  break;
		last = next;
	      }
	  }
  
	int index = dis.readUnsignedShort();
  // CpoolEntry entry = ctype.constants.getPoolEntry(index);
	CpoolUtf8 nameConstant = (CpoolUtf8)
	  ctype.constants.getForced(index, ConstantPool.UTF8);

	int length = dis.readInt();
	nameConstant.intern();
	Attribute attr = readAttribute(dis, nameConstant.string, length, container);
	if (attr != null)
	  {
	    if (attr.getNameIndex() == 0)
	      attr.setNameIndex(index);
	    if (last == null)
	      container.setAttributes(attr);
	    else
	      {
		if (container.getAttributes()==attr)
		  { // Move to end.
		    container.setAttributes(attr.getNext());
		    attr.setNext(null);
		  }
		last.setNext(attr);
	      }
	    last = attr;
	  }
  
      }
  }

  public Attribute readAttribute (DataInputStream dis, String name, int length, AttrContainer container)
      throws IOException
  {
    if (length > 0) dis.skipBytes(length);
    return null;
  /*
    if (name == "SourceFile" && container instanceof ClassType)
      {
	return new SourceFileAttr(dis.readUnsignedShort(), (ClassType) container);
      }
    else if (name == "Code" && container instanceof Method)
      {
  CodeAttr code = new CodeAttr((Method) container);
  code.fixup_count = -1;
	code.setMaxStack(dis.readUnsignedShort());
	code.setMaxLocals(dis.readUnsignedShort());
	int code_len = dis.readInt();
	byte[] insns = new byte[code_len];
	dis.readFully(insns);
	code.setCode(insns);
	int exception_table_length = dis.readUnsignedShort();
	for (int i = 0;  i < exception_table_length;  i++)
	  {
	    int start_pc = dis.readUnsignedShort();
	    int end_pc = dis.readUnsignedShort();
	    int handler_pc = dis.readUnsignedShort();
	    int catch_type = dis.readUnsignedShort();
	    code.addHandler(start_pc, end_pc, handler_pc, catch_type);
	  }
	readAttributes(dis, code);
	return code;
      }
    else if (name == "LineNumberTable" && container instanceof CodeAttr)
      {
  int count = 2 * dis.readUnsignedShort();
	short[] numbers = new short[count];
	for (int i = 0;  i < count;  i++)
	  {
	    numbers[i] = dis.readShort();
	  }
	return new LineNumbersAttr(numbers, (CodeAttr) container);
      }
    else if (name == "LocalVariableTable" && container instanceof CodeAttr)
      {
	CodeAttr code = (CodeAttr) container;
	LocalVarsAttr attr = new LocalVarsAttr(code);
	Method method = attr.getMethod();
	if (attr.parameter_scope == null)
	  attr.parameter_scope = method.pushScope();
	Scope scope = attr.parameter_scope;
	if (scope.end == null)
	  scope.end = new Label(code.PC);
	ConstantPool constants = method.getConstants();
  int count = dis.readUnsignedShort();
	int prev_start = scope.start.position;
	int prev_end = scope.end.position;
	for (int i = 0;  i < count;  i++)
	  {
	    Variable var = new Variable();
	    int start_pc = dis.readUnsignedShort();
	    int end_pc = start_pc + dis.readUnsignedShort();

	    if (start_pc != prev_start || end_pc != prev_end)
	      {
		while (scope.parent != null
		       && (start_pc < scope.start.position
			   || end_pc > scope.end.position))
		  scope = scope.parent;
		Scope parent = scope;
		scope = new Scope(new Label(start_pc), new Label(end_pc));
		scope.linkChild(parent);
		prev_start = start_pc;
		prev_end = end_pc;
	      }
	    scope.addVariable(var);
	    var.setName(dis.readUnsignedShort(), constants);
	    // var.setSignature(dis.readUnsignedShort(), constants);
      var.signature_index = dis.readUnsignedShort();
	    var.offset = dis.readUnsignedShort();
	  }
	return attr;
      }
    else if (name == "Signature" && container instanceof Member)
      {
	return new SignatureAttr(dis.readUnsignedShort(), (Member) container);
      }
    else if (name == "StackMapTable" && container instanceof CodeAttr)
      {
        byte[] data = new byte[length];
        dis.readFully(data, 0, length);
        return new StackMapTableAttr(data, (CodeAttr) container);
      }
    else if ((name == "RuntimeVisibleAnnotations"
              || name == "RuntimeInvisibleAnnotations")
             && (container instanceof Field
                 || container instanceof Method
                 || container instanceof ClassType))
      {
        int numEntries = dis.readUnsignedShort();
        AnnotationEntry[] entries = new AnnotationEntry[numEntries];
        for (int i = 0;  i < numEntries; i++)
          {
            entries[i] = RuntimeAnnotationsAttr.readAnnotationEntry(dis, container.getConstants());
          }
        return new RuntimeAnnotationsAttr(name, entries, numEntries, container);
      }
    else if (name == "ConstantValue" && container instanceof Field)
      {
	return new ConstantValueAttr(dis.readUnsignedShort());
      }
    else if (name == "InnerClasses" && container instanceof ClassType)
      {
  int count = 4 * dis.readUnsignedShort();
	short[] data = new short[count];
	for (int i = 0;  i < count;  i++)
	  {
	    data[i] = dis.readShort();
	  }
	return new InnerClassesAttr(data, (ClassType) container);
     }
    else if (name == "EnclosingMethod" && container instanceof ClassType)
      {
  int class_index = dis.readUnsignedShort();
  int method_index = dis.readUnsignedShort();
	return new EnclosingMethodAttr(class_index, method_index, (ClassType) container);
     }
    else if (name == "Exceptions" && container instanceof Method)
      {
	Method meth = (Method)container;
	int count = dis.readUnsignedShort();
        short[] exn_indices = new short[count];
	for (int i = 0; i < count; ++i)
	  exn_indices[i] = dis.readShort();
        meth.setExceptions(exn_indices);
	return meth.getExceptionAttr();
      }
    else if (name == "SourceDebugExtension" && container instanceof ClassType)
      {
	SourceDebugExtAttr attr
	  = new SourceDebugExtAttr((ClassType) container);
	byte[] data = new byte[length];
	dis.readFully(data, 0, length);
	attr.data = data;
	attr.dlength = length;
	return attr;
      }
    else if (name == "AnnotationDefault" && container instanceof Method)
      {
  AnnotationEntry.Value value = RuntimeAnnotationsAttr.readAnnotationValue(dis, container.getConstants());
  return new AnnotationDefaultAttr(name, value, container);
      }
    else
      {
	byte[] data = new byte[length];
	dis.readFully(data, 0, length);
	return new MiscAttr(name, data);
      }
  */
  }

  protected CpoolClass getClassConstant (int index)
  {
    return ctype.constants.getForcedClass(index);
  }
  
  public void readClassFile ()
      throws IOException
  {
  DataInputStream dis = getDataInputStream (this.cname);
  if (dis == null) return;
  String name = this.cname;
  try
    {
    if (!readHeader(dis))
      throw new ClassFormatError("invalid magic number");
    readVersion(dis);
    readConstants(dis);
    readClassInfo(dis);
    readInterfaces(dis);
    readFields(dis); 
    readMethods(dis); 
    readAttributes(dis, ctype);
  
    if (false)
      {
    System.err.printf ("%s - version: 0x%x\n", name, ctype.classfileFormatVersion);
    if (ctype.constants != null)
      System.err.printf ("%s - constants length: %d\n", name, ctype.constants.getCount());
    else
      System.err.printf ("%s - constants : %d\n", name, ctype.constants);
    System.err.printf ("%s - access_flag: 0x%x, thisClassIndex: %d, superClassIndex: %d, %s\n",
      name, ctype.access_flags, ctype.thisClassIndex, ctype.superClassIndex, ctype.superClassName);
    if (ctype.interfaces != null)
      System.err.printf ("%s - interfaces length: %d\n", name, ctype.interfaces.length);
    else
      System.err.printf ("%s - interfaces : %d\n", name, ctype.interfaces);
    System.err.printf ("%s - fields length: %d\n", name, ctype.fields_count);
    System.err.printf ("%s - methods length: %d\n", name, ctype.methods_count);
    System.err.printf ("%s - attributes\n", name);
      }
    }
  catch (Exception ex)
    {
    throw new WrappedException(ex);
    }
  finally
    {
    if (dis != null) dis.close();
    }
  }
}
