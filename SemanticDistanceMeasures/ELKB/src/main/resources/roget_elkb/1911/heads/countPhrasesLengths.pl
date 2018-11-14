#!/usr/bin/perl -w

$read = 0;
$words = 0;
$lines = 0;

while(<STDIN>){
	if(/<pos number=\"6\" type=\"PHR\.\">/){
		$read = 1;
	}
	elsif(/<\/pos>/){
		$read = 0;
	}
	if((/<word.*?>(.*?)<\/word>/) && ($read == 1)){
		@wrd = split(/\s+/, $1);
		$words += @wrd;
		$lines++;
	}
}

$final = $words/$lines;
print "$words / $lines\n";
print "= $final\n";
