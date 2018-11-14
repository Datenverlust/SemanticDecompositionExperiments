#!/usr/bin/perl -w

for($i = 1; $i <= 1044; $i++){
    `xmllint -valid -noout head$i.xml`;
}
