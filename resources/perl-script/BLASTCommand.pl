use strict;

my $type = $ARGV[0];
my $db = $ARGV[1];
my $accession = $ARGV[2];
my $params = $ARGV[3];
my $format = $ARGV[4];

print "running command <path-to-BLAST>/$type  -db $db -query <path-to-IO>/$accession.fa -out <path-to-IO>/$accession.xml -outfmt '$format' $params -max_target_seqs 10 \n";

system("<path-to-BLAST>/$type  -db $db -query <path-to-IO>/$accession.fa -outfmt '$format' -out <path-to-IO>/$accession.xml $params -max_target_seqs 10");
if ( $? == -1 )
{
  print "command 1 failed: $!\n";
}
else
{
  printf "command 1 exited with value %d", $? >> 8;
}

system("perl <path-to-Perl>/loadBLAST.pl '<path-to-IO>/$accession.xml' '$format' >  <path-to-IO>/$accession.info");

if ( $? == -1 )
{
  print "command 2 failed: $!\n";
}
else
{
  printf "command 2 exited with value %d", $? >> 8;
}

print "finish command\n";


print "loading data command\nperl loadBLAST.pl '<path-to-IO>/$accession.xml' '$format'\n";

print "loading data command\n";
