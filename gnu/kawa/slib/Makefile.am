## Process this with automake to create Makefile.in

include $(top_srcdir)/Make-rules

PACKAGE_FNAME = gnu-kawa-slib
EXTRA_GCJ_INPUTS =  *.class

if ENABLE_XML
XML_SCM = XML.scm XStrings.scm
endif

if WITH_SWING
SWING_SCM = swing.scm
endif

if WITH_AWT
AWT_SCM = gui.scm
endif

# NOTE if this list is changed, also update build.xml.
java_SCM = DefineRecordType.scm enums.scm readtable.scm \
  srfi1.scm srfi2.scm \
  conditions.scm srfi13.scm srfi14.scm srfi34.scm \
  srfi37.scm srfi69.scm pregexp.scm \
  Streams.scm StreamsDerived.scm StreamsPrimitive.scm StreamsType.scm \
  genwrite.scm pp.scm ppfile.scm printf.scm syntaxutils.scm \
  cut.scm testing.scm $(XML_SCM) $(AWT_SCM) $(SWING_SCM)
EXTRA_DIST = $(java_SCM) XML.scm swing.scm
SCM_COMPILE_FLAGS = --warn-undefined-variable --warn-unknown-member --warn-as-error

all: Makefile scm-classes.stamp $(@GCJ_COMPILED_SELECTED@)

