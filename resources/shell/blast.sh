#!/bin/bash
#SBATCH -p debug  # Select Partition /  Queue
#SBATCH -c 2           # Number of cores needed
#SBATCH --mem 8192     # Memory Needed#SBATCH -t 0-00:12     # time (D-HH:MM) <E2><80><93> 12 minute job
#SBATCH -o /usr/users/ga002/tgacbrowser/%j.out    # STDOUT
#SBATCH -e /usr/users/ga002/tgacbrowser/%j.err    # STDERR

cd /tgac/services/browser/software/script/BLAST

echo $1

perl readBLASTparams.pl $1