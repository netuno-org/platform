#!/bin/bash

mvn -Dtest=BuildLibraryTest test

trash-put ../netuno.docs/docs/library/objects
trash-put ../netuno.docs/docs/library/resources

trash-put ../netuno.docs/website/translated_docs/pt-PT/library/objects
trash-put ../netuno.docs/website/translated_docs/pt-PT/library/resources

cp -r docs/EN/library/objects ../netuno.docs/docs/library/objects
cp -r docs/EN/library/resources ../netuno.docs/docs/library/resources

cp -r docs/PT/library/objects ../netuno.docs/website/translated_docs/pt-PT/library/objects
cp -r docs/PT/library/resources ../netuno.docs/website/translated_docs/pt-PT/library/resources

