#!/bin/bash

cd ..

./mvn-install.sh

cd netuno.tritao

mvn -Dtest=BuildLibraryTest test

trash-put ../../doc/docs/library/objects
trash-put ../../doc/docs/library/resources

trash-put ../../doc/website/translated_docs/pt-PT/library/objects
trash-put ../../doc/website/translated_docs/pt-PT/library/resources

cp -r docs/EN/library/objects ../../doc/docs/library/objects
cp -r docs/EN/library/resources ../../doc/docs/library/resources

cp -r docs/PT/library/objects ../../doc/website/translated_docs/pt-PT/library/objects
cp -r docs/PT/library/resources ../../doc/website/translated_docs/pt-PT/library/resources

