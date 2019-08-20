BUILDLIB = build/libs
JARNAME = cadc-wcs-2.1.jar
BUILDJAR = $(BUILDLIB)/$(JARNAME)
GSRC = src/main/java/ca/nrc/cadc/wcs
XTRALIBS = xtralibs/log4j-api-2.12.1.jar:xtralibs/log4j-core-2.12.1.jar

.PHONY: help build clean

help:
	@echo "Make what? Try: build, clean, testjar, jar"

build:
	gradle --info -x test clean wcsJar

clean:
	gradle clean

testjar: $(BUILDJAR)
	java -cp $(BUILDJAR):$(XTRALIBS) ca.nrc.cadc.wcs.Main -v

macjar: $(GSRC)
	gradle -x test clean wcsJar -Pos=darwin

jar: $(BUILDJAR)

$(BUILDJAR): $(GSRC)
	gradle -x test clean wcsJar

%:
	@:
