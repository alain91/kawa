## Process this with automake to create Makefile.in

KAWALIB = ../kawa-$(VERSION).jar
KAWARTLIB = ../kawart-$(VERSION).jar
KAWA = ../bin/kawa.sh
CPJAVA = CLASSPATH="..$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(JAVA)
CPKAWA = CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(KAWA)
KAWATEST = CLASSPATH=".$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(KAWA) -e '(load "testing.zip")' $(OPTIONS)
# Used to test chameneosredux
NDIFF = ndiff
DIFF = diff --strip-trailing-cr

if ENABLE_SERVLET
if ENABLE_XQUERY
SERVLET_TESTS = check-HelloCgiS check-HelloCgiK check-HelloCgiX
else
SERVLET_TESTS = check-HelloCgiS check-HelloCgiK
endif
endif
if WITH_SAX2
SAX2_TESTS = check-MySAXApp check-jaxb-annotations3
endif

WARN_FLAGS = --warn-undefined-variable --warn-invoke-unknown-method --warn-as-error

all:

check: check2-no-full-tailcalls check1 check2-full-tailcalls check-no-inline \
  check-interpreted-cycle check-interpreted-cycleb check-interpreted-cyclebr \
  $(SERVLET_TESTS) $(SAX2_TESTS) \
  check-inlining check-jmc check-use-slots check-load-require check-sva31180 \
  check-bridge

check2-no-full-tailcalls:
	$(MAKE) check2 OPTIONS=--no-full-tailcalls

check2-full-tailcalls:
	$(MAKE) check2 OPTIONS=--full-tailcalls

check-no-inline:
	$(MAKE) check2 OPTIONS=--no-inline

check1:  check-num check-arr check-java-array check-lazy check-lib \
  check-srfi-16 check-srfi-38 check-srfi-64 check-srfi-69 \
  check-srfi-108 check-srfi-109 \
  check-format check-ppfile check-text check-regex check-xml scribble.class \
  check-run-test-scripts check-moduleM \
  check-annotations1 check-annotations2\
  check-binarytrees check-chameneosredux check-fannkuch check-fannkuchredux check-fasta \
  check-knucleotide check-mandelbrot check-meteor check-nbody \
  check-nsieve check-pidigits check-regexdna check-revcomp \
  check-spectralnorm check-threadring
check2:  check-scm check-r7rs check-misc check-mac check-obj check-polytype

check-run-test-scripts:
	CLASSPATH=$(top_builddir) $(JAVA) gnu.kawa.util.RunTestScript \
	  `for test in $(SCRIPTS_TO_RUN); do echo $(srcdir)/$$test; done`

testing.zip: $(srcdir)/testing.scm
	$(KAWA) -e '(compile-file "'$(srcdir)/testing.scm'" "testing.zip")'

check-scm: testing.zip
	rm -rf tmp?
	$(KAWATEST) \
	  -e '(define this-file-name "$(srcdir)/test.scm")' \
	  -e "(set! symbol-read-case 'D)" --no-warn-undefined-variable \
	  -f $(srcdir)/test.scm -e '(test-report)'

check-r7rs:
	$(KAWA) --no-warn-void-used -f $(srcdir)/r7rs-tests.scm

check-num:
	$(KAWA) $(OPTIONS) "$(srcdir)/num-test.scm"

check-arr:
	$(KAWA) $(OPTIONS) "$(srcdir)/arr-test.scm"

check-java-array:
	$(KAWA) $(OPTIONS) "$(srcdir)/java-array-test.scm"

check-lazy:
	$(KAWA) $(OPTIONS) "$(srcdir)/lazy-test.scm"

check-lib:
	$(KAWA) $(OPTIONS) "$(srcdir)/lib-test.scm"

check-srfi-16:
	$(KAWA) $(OPTIONS) "$(srcdir)/srfi-16-test.scm"

check-srfi-38:
	$(KAWA) $(OPTIONS) "$(srcdir)/srfi-38-test.scm"

check-srfi-64:
	$(KAWA) $(OPTIONS) "$(srcdir)/srfi-64-test.scm"

check-srfi-69:
	$(KAWA) $(OPTIONS) "$(srcdir)/srfi-69-test.scm"

check-srfi-108:
	$(KAWA) $(OPTIONS) "$(srcdir)/srfi-108-test.scm"

check-srfi-109:
	$(KAWA) $(OPTIONS) "$(srcdir)/srfi-109-test.scm"

check-text:
	$(KAWA) $(OPTIONS) -e '(set! port-char-encoding "UTF-8")' -f "$(srcdir)/text-test.scm"

check-regex:
	$(KAWA) $(OPTIONS) "$(srcdir)/regex-test.scm"

check-xml:
	$(KAWA) $(OPTIONS) "$(srcdir)/xml-test.scm"

check-misc: testing.zip moduleFT.class InliningTest.class \
	    cycle1.class cycle2.class
	$(KAWATEST) --no-warn-undefined-variable --no-warn-unused \
	  -f "$(srcdir)/misc-test.scm" -e '(test-report)'

check-mac: testing.zip
	$(KAWATEST) \
	  -e '(define src-prefix "$(srcdir)/")' -f "$(srcdir)/mac-test.scm" -e '(test-report)'

check-obj: testing.zip module2.class module3.class classes2.class \
  MyFunc.class MyModule.class cycle1.class cycle2.class
	$(KAWATEST) --no-warn-unknown-member -f "$(srcdir)/obj-test.scm" -e '(test-report)'

check-polytype:
	$(KAWA) "$(srcdir)/polytype.scm"

check-format: testing.zip
	$(KAWA) "$(srcdir)/formatst.scm"

check-ppfile:
	$(KAWA) -e "(require 'pprint-file)" \
	  -e '(define out (open-output-file "tmpP"))' \
	  -e '(pprint-file "@top_srcdir@/gnu/kawa/slib/ppfile.scm" out)' \
	  -e '(close-output-port out)'
	diff tmpP $(srcdir)/ppfile.out
	rm tmpP

scribble.class: $(srcdir)/scribble.scm
	$(KAWA) --applet -C $(srcdir)/scribble.scm

moduleFT.class: $(srcdir)/moduleFT.scm
	CLASSPATH=".$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(KAWA) --full-tailcalls -C $(srcdir)/moduleFT.scm

moduleM0.class: $(srcdir)/moduleM.scm SimpleB.class
	$(KAWA) -T moduleM0 -C $(srcdir)/moduleM.scm
moduleM1.class: moduleM.scm SimpleB.class
	$(KAWA) -T moduleM1 --main -C $(srcdir)/moduleM.scm
check-moduleM-immediate: SimpleB.class
	CLASSPATH=".$(CLASSPATH_SEPARATOR).." $(JAVA) kawa.repl $(srcdir)/moduleM.scm > moduleM-immediate.log
	@if $(DIFF) $(srcdir)/moduleM.expected moduleM-immediate.log; \
	  then echo '# moduleM-immediate test passes'; \
	  else echo FAIL moduleM-immediate test; fi
check-moduleM-run: moduleM0.class
	CLASSPATH=".$(CLASSPATH_SEPARATOR).." $(JAVA) kawa.repl moduleM0 > moduleM-run.log
	@if $(DIFF) $(srcdir)/moduleM.expected moduleM-run.log; \
	  then echo '# moduleM-run test passes'; \
	  else echo FAIL moduleM-run test; fi
check-moduleM-main-app: moduleM1.class
	CLASSPATH=".$(CLASSPATH_SEPARATOR).." $(JAVA) moduleM1 > moduleM-main-app.log
	@if $(DIFF) $(srcdir)/moduleM.expected moduleM-main-app.log; \
	  then echo '# moduleM-main-app test passes'; \
	  else echo FAIL moduleM-main-app test; fi
check-moduleM-main-run: moduleM1.class
	CLASSPATH=".$(CLASSPATH_SEPARATOR).." $(JAVA) kawa.repl moduleM1 > moduleM-main-run.log
	@if $(DIFF) $(srcdir)/moduleM.expected moduleM-main-run.log; \
	  then echo '# moduleM-main-run test passes'; \
	  else echo FAIL moduleM-main-run test; fi
check-moduleM: check-moduleM-immediate check-moduleM-run check-moduleM-main-app check-moduleM-main-run

module0.class: $(srcdir)/module0.scm
	CLASSPATH=".$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(KAWA) -C $(srcdir)/module0.scm

module1.class: $(srcdir)/module1.scm module0.class
	CLASSPATH=".$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(KAWA) -C $(srcdir)/module1.scm

module1a.class: $(srcdir)/module1a.scm
	CLASSPATH=".$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(KAWA) -C $(srcdir)/module1a.scm

module2.class: $(srcdir)/module2.scm module1.class module1a.class
	CLASSPATH=".$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(KAWA) -C $(srcdir)/module2.scm

module3.class: $(srcdir)/module3.scm module1.class MyTimestamp.class
	CLASSPATH=".$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(KAWA) -C $(srcdir)/module3.scm

classes1.class: $(srcdir)/classes1.scm
	CLASSPATH=".$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(KAWA) -C $(srcdir)/classes1.scm

SimpleB.class: $(srcdir)/SimpleB.java classes1.class
	CLASSPATH="..$(CLASSPATH_SEPARATOR).$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(JAVAC) -g $(srcdir)/SimpleB.java -d .

classes2.class: $(srcdir)/classes2.scm SimpleB.class
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) -C $(srcdir)/classes2.scm

HelloCgiK.class: $(srcdir)/HelloCgiK.krl
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) --servlet --krl -C $(srcdir)/HelloCgiK.krl

pidigits.class: $(srcdir)/pidigits.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) --main -C $(srcdir)/pidigits.scm

check-pidigits: pidigits.class
	$(KAWA) pidigits 27 >pidigits27.log
	@if $(DIFF) $(srcdir)/pidigits-output.txt pidigits27.log; \
	  then echo '# pidigits test passes'; \
	  else echo FAIL pidigits test; fi

binarytrees.class: $(srcdir)/binarytrees.scm
	$(CPKAWA) --main -C $(srcdir)/binarytrees.scm

check-binarytrees: binarytrees.class
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) binarytrees 10 >binarytrees10.log
	@if $(DIFF) $(srcdir)/binarytrees-output.txt binarytrees10.log; \
	  then echo '# binarytrees test passes'; \
	  else echo FAIL binarytrees test; fi

chameneosredux.class: $(srcdir)/chameneosredux.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) --main -C $(srcdir)/chameneosredux.scm

check-chameneosredux: chameneosredux.class
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) chameneosredux 600 >chameneosredux600.log
	@if $(NDIFF) -fields 2-10 $(srcdir)/chameneosredux-output.txt chameneosredux600.log; \
	  then echo '# chameneosredux test passes'; \
	  else echo FAIL chameneosredux test; fi

fannkuch.class: $(srcdir)/fannkuch.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) --main -C $(srcdir)/fannkuch.scm

check-fannkuch: fannkuch.class
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) fannkuch 7 >fannkuch7.log
	@if $(DIFF) $(srcdir)/fannkuch-output.txt fannkuch7.log; \
	  then echo '# fannkuch test passes'; \
	  else echo FAIL fannkuch test; fi

fannkuchredux.class: $(srcdir)/fannkuchredux.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) --main -C $(srcdir)/fannkuchredux.scm

check-fannkuchredux: fannkuchredux.class
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) fannkuchredux 7 >fannkuchredux7.log
	@if $(DIFF) $(srcdir)/fannkuchredux-output.txt fannkuchredux7.log; \
	  then echo '# fannkuch-redux test passes'; \
	  else echo FAIL fannkuch-redux test; fi

fasta.class: $(srcdir)/fasta.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) $(WARN_FLAGS) --main -C $(srcdir)/fasta.scm

check-fasta: fasta.class
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) fasta 1000 >fasta1000.log
	@if $(DIFF) $(srcdir)/fasta-output.txt fasta1000.log; \
	  then echo '# fasta test passes'; \
	  else echo FAIL fasta test; fi

knucleotide-input.txt: fasta.class
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) fasta 25000 >knucleotide-input.txt

knucleotide1.class: $(srcdir)/knucleotide.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) $(WARN_FLAGS) --main -T knucleotide1 -C $(srcdir)/knucleotide.scm

knucleotide2.class: $(srcdir)/knucleotide-2.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) $(WARN_FLAGS) --main -T knucleotide2 -C $(srcdir)/knucleotide-2.scm

check-knucleotide-1: knucleotide1.class knucleotide-input.txt
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) knucleotide1 < knucleotide-input.txt >tmp-knucleotide1.log
	@if $(DIFF) $(srcdir)/knucleotide-output.txt tmp-knucleotide1.log; \
	  then echo '# knucleotide1 test passes'; \
	  else echo FAIL knucleotide1 test; fi
check-knucleotide-2: knucleotide2.class knucleotide-input.txt
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) knucleotide2 < knucleotide-input.txt >tmp-knucleotide2.log
	@if $(DIFF) $(srcdir)/knucleotide-output.txt tmp-knucleotide2.log; \
	  then echo '# knucleotide2 test passes'; \
	  else echo FAIL knucleotide2 test; fi
check-knucleotide: check-knucleotide-1 check-knucleotide-2

meteor.class: $(srcdir)/meteor.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) $(WARN_FLAGS) --main -C $(srcdir)/meteor.scm

check-meteor: meteor.class
	$(KAWA) meteor 2098 >meteor2098.log
	@if $(DIFF) $(srcdir)/meteor-output.txt meteor2098.log; \
	  then echo '# meteor test passes'; \
	  else echo FAIL meteor test; fi

nbody.class: $(srcdir)/nbody.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) $(WARN_FLAGS) --main -C $(srcdir)/nbody.scm

check-nbody: nbody.class
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) nbody 1000 >nbody1000.log
	@if $(DIFF) $(srcdir)/nbody-output.txt nbody1000.log; \
	  then echo '# nbody test passes'; \
	  else echo FAIL nbody test; fi

nsieve.class: $(srcdir)/nsieve.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) --main -C $(srcdir)/nsieve.scm

check-nsieve: nsieve.class
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) nsieve 7 >nsieve7.log
	@if $(DIFF) $(srcdir)/nsieve-output.txt nsieve7.log; \
	  then echo '# nsieve test passes'; \
	  else echo FAIL nsieve test; fi

mandelbrot.class: $(srcdir)/mandelbrot.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) --main -C $(srcdir)/mandelbrot.scm

check-mandelbrot: mandelbrot.class
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) mandelbrot 200 >tmp-mandelbrot-200.bmp
	@if $(DIFF) $(srcdir)/mandelbrot-output.bmp tmp-mandelbrot-200.bmp; \
	  then echo '# mandelbrot test passes'; \
	  else echo FAIL mandelbrot test; fi

regexdna.class: $(srcdir)/regexdna.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) --main -C $(srcdir)/regexdna.scm

regexdna-input.txt: fasta.class
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) fasta 10000 >regexdna-input.txt

check-regexdna: regexdna.class regexdna-input.txt
	$(CPJAVA) regexdna < regexdna-input.txt >tmp-regexdna.log
	@if $(DIFF) $(srcdir)/regexdna-output.txt tmp-regexdna.log; \
	  then echo '# regexdna test passes'; \
	  else echo FAIL regexdna test; fi

revcomp-input.txt: fasta.class
	$(CPJAVA) fasta 1000 >revcomp-input.txt

revcomp1.class: $(srcdir)/revcomp.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) $(WARN_FLAGS) --main -T revcomp1 -C $(srcdir)/revcomp.scm

revcomp2.class: $(srcdir)/revcomp-2.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) $(WARN_FLAGS) --main -T revcomp2 -C $(srcdir)/revcomp-2.scm

check-revcomp-1: revcomp1.class revcomp-input.txt
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) revcomp1 < revcomp-input.txt >tmp-revcomp1.log
	@if $(DIFF) $(srcdir)/revcomp-output.txt tmp-revcomp1.log; \
	  then echo '# revcomp-1 test passes'; \
	  else echo FAIL revcomp-1 test; fi
check-revcomp-2: revcomp2.class revcomp-input.txt
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) revcomp2 < revcomp-input.txt >tmp-revcomp2.log
	@if $(DIFF) $(srcdir)/revcomp-output.txt tmp-revcomp2.log; \
	  then echo '# revcomp-2 test passes'; \
	  else echo FAIL revcomp-2 test; fi
check-revcomp: check-revcomp-1 check-revcomp-2

spectralnorm.class: $(srcdir)/spectralnorm.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) --main -C $(srcdir)/spectralnorm.scm

check-spectralnorm: spectralnorm.class
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) spectralnorm 100 >tmp-spectralnorm100.log
	@if $(DIFF) $(srcdir)/spectralnorm-output.txt tmp-spectralnorm100.log; \
	  then echo '# spectralnorm test passes'; \
	  else echo FAIL spectralnorm test; fi

threadring.class: $(srcdir)/threadring.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) $(WARN_FLAGS) --main -C $(srcdir)/threadring.scm

check-threadring: threadring.class
	CLASSPATH="$(KAWARTLIB)$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(JAVA) threadring 1000 >threadring1000.log
	@if $(DIFF) $(srcdir)/threadring-output.txt threadring1000.log; \
	  then echo '# threadring test passes'; \
	  else echo FAIL threadring test; fi

HelloCgiK:
	$(LN_S) ../bin/cgi-servlet HelloCgiK

check-HelloCgiK: HelloCgiK.class HelloCgiK
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	REQUEST_METHOD=GET SCRIPT_NAME=HelloCgiK SERVER_NAME=localhost \
	  KAWALIB=$(KAWALIB) ./HelloCgiK >tmp.out
	@if $(DIFF) $(srcdir)/HelloCgiK.expected tmp.out; \
	  then echo '# HelloCgiK test passes'; \
	  else echo FAIL HelloCgiK test; fi

HelloCgiS.class: $(srcdir)/HelloCgiS.scm
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) --servlet --output-format xml -C $(srcdir)/HelloCgiS.scm

HelloCgiS:
	$(LN_S) ../bin/cgi-servlet HelloCgiS

check-HelloCgiS: HelloCgiS.class HelloCgiS
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	REQUEST_METHOD=GET SCRIPT_NAME=HelloCgiS SERVER_NAME=localhost \
	  KAWALIB=$(KAWALIB) ./HelloCgiS >tmp.out
	@if $(DIFF) $(srcdir)/HelloCgiS.expected tmp.out; \
	  then echo '# HelloCgiS test passes'; \
	  else echo FAIL HelloCgiS test; fi

HelloCgiX.class: $(srcdir)/HelloCgiX.xql
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) --servlet -C $(srcdir)/HelloCgiX.xql

HelloCgiX:
	$(LN_S) ../bin/cgi-servlet HelloCgiX

check-HelloCgiX: HelloCgiX.class HelloCgiX
	CLASSPATH="@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	REQUEST_METHOD=GET SCRIPT_NAME=HelloCgiX SERVER_NAME=localhost \
	  PATH_TRANSLATED=./HelloCgiX KAWALIB=$(KAWALIB) ./HelloCgiX >tmp.out
	@if $(DIFF) -b $(srcdir)/HelloCgiX.expected tmp.out; \
	  then echo '# HelloCgiX test passes'; \
	  else echo FAIL HelloCgiX test; fi

MySAXApp.class: $(srcdir)/MySAXApp.java
	CLASSPATH="..$(CLASSPATH_SEPARATOR).$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(JAVAC) -g $(srcdir)/MySAXApp.java -d .

check-MySAXApp: MySAXApp.class
	$(CPJAVA) -Dorg.xml.sax.driver=gnu.kawa.sax.KawaXMLReader\
	  MySAXApp ../gnu/xquery/testsuite/reviews.xml > tmp.out
	@if $(DIFF) -b $(srcdir)/MySAXApp.expected tmp.out; \
	  then echo '# MySAXApp test passes'; \
	  else echo FAIL MySAXApp test; fi

MyFunc.class MyModule.class: $(srcdir)/MyFunc.java $(srcdir)/MyModule.java
	CLASSPATH="..$(CLASSPATH_SEPARATOR).$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(JAVAC) -g -d . $?

MyDate.class MyTimestamp.class: $(srcdir)/MyDate.java $(srcdir)/MyTimestamp.java
	CLASSPATH="..$(CLASSPATH_SEPARATOR).$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(JAVAC) -g -d . $?

check-pitfalls:
	$(CPKAWA) -f $(srcdir)/r5rs_pitfall.scm

MyAnnotType.class: $(srcdir)/MyAnnotType.java
	CLASSPATH="..$(CLASSPATH_SEPARATOR).$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(JAVAC) -g -d . $?
annotations1.class: $(srcdir)/annotations1.scm MyAnnotType.class
	CLASSPATH=".$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(KAWA) -C $<
tmp-annotations1.bytecode: annotations1.class
	$(CPJAVA) gnu.bytecode.dump 'annotations1$$MyClass'.class >tmp-annotations1.bytecode
check-annotations1: tmp-annotations1.bytecode
	@if $(DIFF) $(srcdir)/annotations1-bytecode.expected tmp-annotations1.bytecode; \
	  then echo '# annotations1 test passes'; \
	  else echo FAIL annotations1 test; fi

tmp-bad-annotations2.err: bad-annotations2.scm MyAnnotType.class
	-$(CPKAWA) -C $< 2>&1 \
        | sed -e '1d' -e 's|^.*/$(<F)|$(<F)|' >tmp-bad-annotations2.err
check-annotations2: tmp-bad-annotations2.err
	@if $(DIFF) $(srcdir)/bad-annotations2.expected tmp-bad-annotations2.err; \
	  then echo '# annotations2 test passes'; \
	  else echo FAIL annotations2 test; fi

JaxbAnnotations3.class: $(srcdir)/jaxb-annotations3.scm
	CLASSPATH="..$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) -d .. -P testsuite. -T JaxbAnnotations3 -C $<
tmp-jaxb-annotations3.out: JaxbAnnotations3.class
	CLASSPATH="..$(CLASSPATH_SEPARATOR)@conf_classpath@.$(CLASSPATH_SEPARATOR)$$CLASSPATH" \
	$(KAWA) testsuite.JaxbAnnotations3 <../gnu/xquery/testsuite/bib.xml >tmp-jaxb-annotations3.out
check-jaxb-annotations3: tmp-jaxb-annotations3.out
	@if $(DIFF) $(srcdir)/jaxb-annotations3.expected tmp-jaxb-annotations3.out; \
	  then echo '# jaxb-annotations3 test passes'; \
	  else echo FAIL jaxb-annotations3 test; fi

APPLETVIEWER = appletviewer

# Not done by "make check" as it is interactive.
check-scribble: scribble.class
	$(APPLETVIEWER) -J-cp -J. -J-cp -J$(KAWARTLIB) scribble.html

HTTPD_PORT = 8888
# Not done by "make check" as it is interactive.
check-server:
	$(CPJAVA) kawa.repl --http-auto-handler / $(srcdir)/webtest/ --http-start $(HTTPD_PORT)

cycle1.class cycle2.class: $(srcdir)/cycle1.scm $(srcdir)/cycle2.scm
	$(CPKAWA) -C $^

check-interpreted-cycle:
	CLASSPATH=.. $(KAWA) \
	  -e '(require "$(srcdir)/cycle1.scm")' \
	  -e '(test-begin "interpreted cycle")' \
	  -e '(test-assert (is-even? 6))' \
	  -e '(test-equal #f (is-even? 7))' \
	  -e '(test-error (is-odd? 4))' \
	  -e '(test-end)'

check-interpreted-cycleb:
	CLASSPATH=.. $(KAWA) $(srcdir)/cycleb1.scm > cycleb.log
	@if $(DIFF) $(srcdir)/cycleb.expected cycleb.log; \
	  then echo '# cycleb test passes'; \
	  else echo FAIL cycleb test; fi

check-interpreted-cyclebr:
	CLASSPATH=.. $(KAWA) -e '(require "$(srcdir)/cycleb1.scm")' > cyclebr.log
	@if $(DIFF) $(srcdir)/cycleb.expected cyclebr.log; \
	  then echo '# cyclebr test passes'; \
	  else echo FAIL cyclebr test; fi

InliningTest.class: $(srcdir)/inlining-test.scm
	$(CPKAWA) -T InliningTest -C $(srcdir)/inlining-test.scm

LIST_CODE_SIZE = $(CPJAVA) gnu.bytecode.ListCodeSize

inlining.log: InliningTest.class cycle1.class cycle2.class \
  binarytrees.class fannkuch.class fannkuchredux.class fasta.class mandelbrot.class nbody.class pidigits.class regexdna.class revcomp1.class revcomp2.class spectralnorm.class Makefile
	$(LIST_CODE_SIZE) InliningTest.class \
	setCar getHome setHome isPair isEmpty castToPair newPair tohex \
	makeIarr lengthIarr getIarr setIarr varargs \
	factoriali1 factoriali2 factoriall1 plusLambda1 firstNegative \
	inlineTwoCalls inlineTwoFunctions checkEven \
	constantPropagation1 constantPropagation2 constantPropagation3 \
	factorialInfer1 getFromVector1 getFromVector2 \
	topLevelRecurse1 topLevelRecurse2 sum1 sum2 numcomp eqv >inlining.log
	$(LIST_CODE_SIZE) cycle1.class isIsEven >>inlining.log
	$(LIST_CODE_SIZE) cycle2.class isIsOdd >>inlining.log
	$(LIST_CODE_SIZE) binarytrees.class bottomUpTree test >>inlining.log
	$(LIST_CODE_SIZE) 'binarytrees$$TreeNode.class' itemCheck >>inlining.log
	$(LIST_CODE_SIZE) fannkuch.class 'vectorReverseSlice$$Ex' countFlips fannkuch >>inlining.log
	$(LIST_CODE_SIZE) fannkuchredux.class fannkuch >>inlining.log
	$(LIST_CODE_SIZE) fasta.class randomNext >>inlining.log
	$(LIST_CODE_SIZE) pidigits.class pidigits >>inlining.log
	$(LIST_CODE_SIZE) 'pidigits$$Transformation.class' next extract compose >>inlining.log
	$(LIST_CODE_SIZE) 'pidigits$$PiDigitSpigot.class' next digit isSafe produce consume >>inlining.log
	$(LIST_CODE_SIZE) mandelbrot.class compute >>inlining.log
	$(LIST_CODE_SIZE) nbody.class A nbodySystem advance energy jupiter >>inlining.log
	$(LIST_CODE_SIZE) 'nbody$$Body.class' offsetMomentum >>inlining.log
	$(LIST_CODE_SIZE) regexdna.class regexdna >>inlining.log
	$(LIST_CODE_SIZE) revcomp1.class revcomp >>inlining.log
	$(LIST_CODE_SIZE) revcomp2.class run >>inlining.log
	$(LIST_CODE_SIZE) 'revcomp2$$RevByteArray'.class reverseAndPrint >>inlining.log
	$(LIST_CODE_SIZE) spectralnorm.class A multiplyAv multiplyAtv multiplyAtAv >>inlining.log

check-inlining: inlining.log $(srcdir)/inlining.expected
	@if $(DIFF) $(srcdir)/inlining.expected inlining.log; \
	  then echo '# inlining test passes'; \
	  else echo FAIL inlining test; fi

jmc.log: $(srcdir)/jmc.scm
	$(KAWA) -f $(srcdir)/jmc.scm >jmc.log

check-jmc: jmc.log $(srcdir)/jmc.expected
	@if $(DIFF) $(srcdir)/jmc.expected jmc.log; \
	  then echo '# bytecompilation test jmc test passes'; \
	  else echo FAIL bytecompilation test jmc; fi

Base.class: $(srcdir)/Base.java
	CLASSPATH="..$(CLASSPATH_SEPARATOR).$(CLASSPATH_SEPARATOR)$$CLASSPATH" $(JAVAC) -g $< -d .

ASupport.class: $(srcdir)/ASupport.scm Base.class
	$(CPKAWA) -C $<

check-use-slots: ASupport.class
	$(KAWA) $(OPTIONS) -f $(srcdir)/use-slots.scm

check-load-require:
	$(KAWA) -e '(load "$(srcdir)/test-load-require.scm")' \
	   -e '(mytest3)' >tmp-load-require.out
	@if $(DIFF) $(srcdir)/test-load-require.expected tmp-load-require.out; \
	  then echo '# check-load-require test passes'; \
	  else echo FAIL check-load-require test; fi
EXTRA_LOAD_REQUIRE_DIST = test-load-require.scm test-load-require-1.scm \
  test-load-require-2.scm  test-load-require-3.scm test-load-require.expected

check-sva31180:
	$(KAWA) -e '(require "$(srcdir)/sva31180.scm")' \
	  -e '(format #t "~a~%~a~%" (foo 5 "foo") (foo2))' > sva31180.log
	sed -n -e 's|;;Output: *\(.*$$\)|\1|p' <$(srcdir)/sva31180.scm >tmp-sva31180.expected
	@if $(DIFF) tmp-sva31180.expected sva31180.log; \
	  then echo '# check-sva31180 passes'; \
	  else echo FAIL check-sva31180; fi

check-bridge:
	$(KAWA) $(OPTIONS) "$(srcdir)/bridge.scm"

CLEANFILES = tmp* *.log testing.zip revcomp-input.txt knucleotide-input.txt regexdna-input.txt *.class HelloCgiK HelloCgiS
SCRIPTS_TO_RUN = uninit1.scm errors1.scm errors2.scm \
  inner-1.scm lambda-in-class-1.scm reflect1.scm promise1.scm \
  anonymous-lambda-1.scm unreach1.scm unreach2.scm unreach3.scm \
  bad-defclass.scm defclass1.scm defclass2.scm bad-voidexp.scm \
  srfi-45-test1.scm srfi-45-test2.scm bad-srfi-109.scm \
  define1.scm mac-obj.scm overload1.scm symbol1.scm \
  command1.scm command2.scm command3.scm \
  sva34005.scm sva35362.scm sva36039.scm sva36413.scm sva36556.scm \
  sva36853.scm sva36863.scm sva36917.scm sva37684.scm sva38026.scm \
  sva39060.scm sva39150.scm sva39945.scm sva39940.scm sva39947.scm \
  sva40077.scm
EXTRA_DIST = testing.scm test-utils.scm \
  test.scm num-test.scm mac-test.scm mac1.scm r7rs-tests.scm \
  classes1.scm classes2.scm SimpleB.java obj-test.scm arr-test.scm \
  lib-test.scm srfi-38-test.scm srfi-64-test.scm srfi-69-test.scm \
  srfi-108-test.scm srfi-109-test.scm \
  text-test.scm regex-test.scm java-array-test.scm lazy-test.scm \
  jmc.scm jmc.expected sva31180.scm use-slots.scm ASupport.scm Base.java \
  MyFunc.java MyModule.java MyDate.java MyTimestamp.java \
  misc-test.scm polytype.scm formatst.scm scribble.scm moduleFT.scm \
  module0.scm module1.scm module1a.scm module2.scm module3.scm included-1.scm \
  moduleM.scm moduleM.expected HelloCgiK.krl HelloCgiS.scm HelloCgiX.xql \
  cycleb1.scm cycleb2.scm cycleb.expected \
  ppfile.out scribble.html.in $(EXTRA_LOAD_REQUIRE_DIST) \
  MySAXApp.java MySAXApp.expected r5rs_pitfall.scm \
  HelloCgiK.expected HelloCgiS.expected HelloCgiX.expected \
  inlining-test.scm inlining.expected cycle1.scm cycle2.scm test-cycle12.scm \
  MyAnnotType.java annotations1.scm annotations1-bytecode.expected\
  bad-annotations2.scm bad-annotations2.expected\
  jaxb-annotations3.scm jaxb-annotations3.expected\
  binarytrees.scm binarytrees-output.txt \
  chameneosredux.scm chameneosredux-output.txt\
  fannkuch.scm fannkuch-output.txt fannkuchredux.scm fannkuchredux-output.txt \
  fasta.scm fasta-output.txt \
  knucleotide.scm knucleotide-2.scm knucleotide-output.txt \
  mandelbrot.scm mandelbrot-output.bmp \
  meteor.scm meteor-output.txt nbody.scm nbody-output.txt \
  nsieve.scm nsieve-output.txt \
  pidigits.scm pidigits-output.txt \
  regexdna.scm regexdna-output.txt \
  revcomp.scm revcomp-2.scm revcomp-output.txt \
  spectralnorm.scm spectralnorm-output.txt \
  threadring.scm threadring-output.txt \
  moduleM.scm moduleM.expected \
  $(SCRIPTS_TO_RUN)
