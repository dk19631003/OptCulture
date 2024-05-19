
if [ $# -ne 1 ]
then
	echo 'parameter is required'
	exit 1
fi
spamassassin -t < $1

