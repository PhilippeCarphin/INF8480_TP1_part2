Commande pour se logger sur le serveur OpenStack a travers un ordi de Poly
	
	ssh -i ~/.ssh/id_rsa ubuntu@132.207.12.84

Sur la machine disante Alex a installé Java JDK 7 et modifié /etc/environment pour que JAVA_HOME pointe sur le jdk 7.

Tout cleané et recompilé et ça marche.


======================= Mercredi 24 ============================

Alex avait beaucoup gossé mais sans résultats, finalement

- Installer Java JDK 7 sur la machine à distance,
- Modifié /etc/environment pour que JAVA_HOME pointe sur le JDK 7 (changer le "8" pour un "7")
- $ ant clean
- $ ant
- $ ./server
	>>> Ça marche
mais on ne peux pas se connecter.

Ensuite:
========
	
	Modifier le fichier server. Il y a une ligne à ajouter à la commande.
	Cette ligne est commentée, il faut la mettre dans la commande et la 
	modifier un peu.  Cette modification est SEULEMENT pour la version
	du fichier ./server qui roule sur la machine distante.

Maintenant,
===========

	on arrive à rouler la commande client de notre ordi de Poly.

	[INF8480_TP1 (master) 30595] $ ./client 132.207.12.84
		Erreur: Le nom 'server' n'est pas défini dans le registre.
		Temps écoulé appel normal: 2023 ns
		Résultat appel normal: 11
		Temps écoulé appel RMI distant: 11724028 ns
		Résultat appel RMI distant: 11
	[INF8480_TP1 (master) 30595] $ 

On arrive aussi à runner le client lorsqu'on est loggé sur la machine distante.

Finalement, pour que tout fonctionne, il faut aussi rouler une instance locale
du serveur.  Pour ce faire, 

1) Aller dans le dossier $TP/bin et starter rmi DE CE DOSSIER.
2) Maintenant, on peut exécuter $TP/server 
	*Note: cette version de $TP/server est la version originale sans
		la modification décrite plus haut.

SUCCES :
========
	
	Maintenant qu'un processus serveur roule localement et sur
	la machine distante, on peut faire 

	[INF8480_TP1 (master) ] $ ./client 132.207.12.84
		Temps écoulé appel normal: 4627 ns
		Résultat appel normal: 11
		Temps écoulé appel RMI local: 1049489 ns
		Résultat appel RMI local: 11
		Temps écoulé appel RMI distant: 1015168 ns
		Résultat appel RMI distant: 11
	[INF8480_TP1 (master) ] $ 


=========================================================
