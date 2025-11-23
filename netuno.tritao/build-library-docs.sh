#!/bin/bash

cd ..

mvn -am -pl netuno.tritao -Drevision=DEV -Dtest=BuildLibraryTest -Dsurefire.failIfNoSpecifiedTests=false test

cd netuno.tritao

trash-put ../../doc/docs/library/objects/*.md
trash-put ../../doc/docs/library/resources/*.md

trash-put ../../doc/i18n/pt/docusaurus-plugin-content-docs/current/library/objects/*.md
trash-put ../../doc/i18n/pt/docusaurus-plugin-content-docs/current/library/resources/*.md

cp -r docs/EN/library/objects/*.md ../../doc/docs/library/objects/
cp -r docs/EN/library/resources/*.md ../../doc/docs/library/resources/

cp -r docs/PT/library/objects/*.md ../../doc/i18n/pt/docusaurus-plugin-content-docs/current/library/objects/
cp -r docs/PT/library/resources/*.md ../../doc/i18n/pt/docusaurus-plugin-content-docs/current/library/resources/

