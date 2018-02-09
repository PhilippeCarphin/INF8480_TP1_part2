################################################################################
# Ce script est utilise pour rendre transparent le fait que le fichier server
# est différent sur la machine distante et sur nos machines locales.
#
# Les deux versions seront dans server_distant et dans server_local
#
# Sur la machine locale, server sera un lien vers server_local et sur la machine
# dstante, le fichier server sera un lien vers server_distant.
################################################################################
if [[ $(hostname) == "inf8480-tp1" ]] ; then
        target=server_distant
else
        target=server_local
fi

if [ -e server ] ; then
	if [ -L server ] ; then
		echo "OK server is already a link"
	else
		echo "Server is a regular file replacing with a link to $target"
		mv server $target
		ln -s $target server
	fi
else
	echo "File server does not exist, creating link to $target"
	ln -s $target server
fi

ls -l server
