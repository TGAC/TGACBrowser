use strict;

my $type = $ARGV[0];
my $db = $ARGV[1];
my $accession = $ARGV[2];
my $params = $ARGV[3];
my $format = $ARGV[4];

print "running command /data/workarea/bianx/blast+/$type  -db $db -query /scratch/tgacbrowser/$accession.fa -out /scratch/tgacbrowser/$accession.xml -outfmt '$format' $params -max_target_seqs 10 \n";

system("/data/workarea/bianx/blast+/$type  -db $db -query /scratch/tgacbrowser/$accession.fa -outfmt '$format' -out /scratch/tgacbrowser/$accession.xml $params -max_target_seqs 10");
if ( $? == -1 )
{
  print "command 1 failed: $!\n";
}
else
{
  printf "command 1 exited with value %d", $? >> 8;
}

system("perl /data/workarea/tgacbrowser/loadBLAST.pl '/scratch/tgacbrowser/$accession.xml' '$format' >  /scratch/tgacbrowser/$accession.info");
if ( $? == -1 )
{
  print "command 2 failed: $!\n";
}
else
{
  printf "command 2 exited with value %d", $? >> 8;
}

print "finish command\n";


print "loading data command\nperl loadBLAST.pl '/scratch/tgacbrowser/$accession.xml' '$format'\n";

#`perl loadBLAST.pl '/scratch/tgacbrowser/$accession.xml' '$format'`;

print "loading data command\n";
