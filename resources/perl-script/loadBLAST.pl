#
#@author: thankia
#
#This script developed to load data into ensemble database from GFF3
#
#created on: 09-07-2013
#
#

use JSON::PP;
use DBI;
use DBD::mysql;

my $xml = $ARGV[0];
my $format = $ARGV[1];

print "$xml \n";
print "$format \n";

my $db = "";
my $link = "";

my $connect = DBI->connect("dbi:mysql:db-name:db-server", "username", "pwd", { RaiseError => 1 });

updateStatus();

getDB();


getLink();


 
open FILE, "<", $xml or loadNoFile();
if($format  eq "6 qseqid sseqid qstart qend bitscore qseq sseq btop"){
	loadResultforTrack();
}else{
print "load results \n";
	loadResult();
}

sub loadNoFile {
	print "Couldn't open file: $ARGV[0]";
	my @id =split(/\//,$ARGV[0]);
	my @filename = split(/\./,$id[-1]);
	my $query1 = $connect->prepare("INSERT INTO blast_result values ('$filename[0]','[\"No hits found.\"]');");
	$query1->execute;
	exit;
}

sub failedSQL {
        print "failed SQL $ARGV[0]";
        my @id =split(/\//,$ARGV[0]);
        my @filename = split(/\./,$id[-1]);
        my $query1 = $connect->prepare("update blast_status set status = \"FAILED\" where id_blast = '$filename[0]'");
        $query1->execute;
        exit;
}

sub loadResult {
print "load result\n";
my @id =split(/\//,$ARGV[0]);
	my @filename = split(/\./,$id[-1]);
	my %hash = ();
	my @jsonarray;
	my $json = JSON::PP->new->allow_nonref;
	while (my $line = <FILE>) {
	chomp($line);
		my (@infm) = split(/\t/,$line);
	$hash{ 'q_id' } = $infm[0]; 
			if($link.length > 0){ 
				$hash{ 's_id' } = "<a target=\"_blank\" href=\"../" + $link + "/index.jsp?query=" + $infm[1] + "&from=" + $infm[8] + "&to=" + $infm[9] + "&blasttrack=" + $filename[0]+ "\\\">" + $infm[1]+ "</a>";  
			}
			else {
				$hash{ 's_id' } = $infm[1]; 
			}
			$hash{ 'identity' } = $infm[2];  
			$hash{ 'aln_length' } = $infm[3];  
			$hash{ 'mismatch' } = $infm[4];  
			$hash{ 'gap_open' } = $infm[5];  
			$hash{ 'q_start' } = $infm[6];  
			$hash{ 'q_end' } = $infm[7];  
			$hash{ 's_start' } = $infm[8];  
			$hash{ 's_end' } = $infm[9];  
			$hash{ 'e_value' } = $infm[10];  
			$hash{ 'bit_score' } = $infm[11];  
			$hash{ 's_db' } = $db;  
			$hash{ 'sequence' } = $infm[12];
		 	$hash{ 'qsequence' } = $infm[13];
		$hash_ref = \%hash;
		my $js = $json->encode($hash_ref);
		push(@jsonarray, $js);
	}
	my $someNames = join(', ', @jsonarray); 
	$someNames = "[$someNames]";
	 print "\n$someNames\n";
	my @id =split(/\//,$ARGV[0]);
	my @filename = split(/\./,$id[-1]);

print "execute";
print "\n array length    $#jsonarray\n";
        if($#jsonarray < 0){
print "if";
	my $query1 = $connect->prepare("INSERT INTO blast_result values ('$filename[0]','[\"No hits found.\"]');");
$query1->execute || failedSQL();
}
        else{
print "else";
	my $query1 = $connect->prepare("INSERT INTO blast_result values ('$filename[0]','$someNames');");
$query1->execute || failedSQL();
        }

print "executed";
}

sub updateStatus {
	my @id =split(/\//,$ARGV[0]);
	my @filename = split(/\./,$id[-1]);
	my $query3 = $connect->prepare("update blast_status set status = 'COMPLETED' where id_blast = '$filename[0]';");
	$query3->execute;
}

sub getDB {
	my @id =split(/\//,$ARGV[0]);
	my @filename = split(/\./,$id[-1]);
	my $query3 = $connect->prepare("select blast_db from blast_params where id_blast = '$filename[0]';");
	my $db_list = $query3->execute;

	if($db_list > 0) {
		while (my @row = $query3 ->fetchrow_array()) {
			$db = $row[0];
		}
	}

        @id =split(/\//,$db);
	@filename = split(/\./,$id[-1]);
	$db = $filename[0];
}

sub getLink {
	my @id =split(/\//,$ARGV[0]);
	my @filename = split(/\./,$id[-1]);
	my $query3 = $connect->prepare("select link from blast_params where id_blast = '$filename[0]';");
	my $link_list = $query3->execute;

	if($link_list > 0) {
		while (my @row = $query3 ->fetchrow_array()) {
			$link = $row[0];
		}
	}
}

sub loadResultforTrack {
print "load resultforTrack\n";
my @id =split(/\//,$ARGV[0]);
	my @filename = split(/\./,$id[-1]);
	my %hash = ();
	my @jsonarray;
	my $json = JSON::PP->new->allow_nonref;
	while (my $line = <FILE>) {
		chomp($line);

		my (@infm) = split(/\s+/,$line);
print ("$#infm\n");
		if($#infm == 7){
			$hash{ 'start' } = $infm[2]; 
			if($link.length > 0){ 
				$hash{'desc'} = "<a target=\"_blank\" href=\"../" + $link + "/index.jsp?query=" + $infm[1] + "&from=" + $infm[2] + "&to=" + $infm[3] + "\\\">" + $infm[1]+ "</a>";  
			}
			else {
				$hash{'desc'} = $infm[1]; 
			}
			$hash{'end'} = $infm[3];  
			$hash{'score'} = $infm[4];  
			$hash{'flag'} = false;  
			$hash{'reverse'} =false;  
			$hash{'indels'} = $json->decode(indels($infm[5], $infm[6], $infm[7]));  
		}
		$hash_ref = \%hash;
		my $js = $json->encode($hash_ref);
		push(@jsonarray, $js);
	}

	my $someNames = join(', ', @jsonarray); 
	$someNames = "[$someNames]";
	my @id =split(/\//,$ARGV[0]);
	my @filename = split(/\./,$id[-1]);
	my $query1 = $connect->prepare("INSERT INTO blast_result values ('$filename[0]','$someNames');");

	$query1->execute || failedSQL();
}

sub indels {
	my $json = JSON::PP->new->allow_nonref;
	my $ins = 0;
	my $query = $_[0];
	my $hit = $_[1];
	my $btop = $_[2];
	$btop =~ s/(\d+)/ $1 /g;
	my @indel_array;
	my @btop_array = split(" ",$btop);

	foreach $var(@btop_array){
		my %indel;
		
		if ($var =~ /^[+-]?\d+$/ ) {
			$ins = $ins+$var;
			#print "Is a number $var $ins\n";
		} 
		else {
			$ins = $ins+(length($var)/2);
			my $from = ($ins-4);
			$indel{'position'} = $ins;
			$indel{'query'} = substr($query, $from, 7);
			$indel{'hit'} = substr($hit, $from, 7);
			$indel_ref = \%indel;
		my $js = $json->encode($indel_ref);
		push(@indel_array, $js);
		}
	}

	my $indels = join(',', @indel_array); 
	$indels = "[$indels]";
	
	return "$indels";
}
