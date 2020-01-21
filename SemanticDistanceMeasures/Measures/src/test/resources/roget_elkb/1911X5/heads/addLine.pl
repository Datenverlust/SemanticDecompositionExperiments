#!/usr/bin/perl -w

foreach $file (@ARGV){
	$data = "";
	open(IN, "<$file") || die "$!:$file\n";
	$data .= <IN>;
	$data.= <IN>;
	$data .= "<?xml-stylesheet href=\"rogets.xsl\" type=\"text/xsl\" ?>\n";
	while(<IN>){
		$data .= $_;
	}
	close(IN);
	open(OUT, ">$file") || die "$!:$file\n";
	print OUT $data;
	close(OUT);
}
