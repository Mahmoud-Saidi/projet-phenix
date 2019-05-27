# projet-phenix

Ce projet permet de déterminer, chaque jour, les 100 produits qui ont les meilleures ventes et ceux qui génèrent le plus gros Chiffre d'Affaire par magasin et en général.Il permet egalement d'avoir ces mêmes indicateurs sur les 7 derniers jours. D'une facon général ce projet génére ces mêmes indicateurs sur les X derniers jours (X nombre de jours a votre choix).

Le projet permet egalement de générer des fichiers de transactions et des fichiers référentiels pour vérifier les contraintes imposées par le projet.  

Le projet contient deux classe principales : ProcessData et GenerateData.

####ProcessData: 
Le main de cette class permet de répondre au besoin fonctionnelle du projet en générant les fichiers : 
top_100_ventes_<ID_MAGASIN>_YYYYMMDD.data
top_100_ventes_GLOBAL_YYYYMMDD.data
top_100_ca_<ID_MAGASIN>_YYYYMMDD.data
top_100_ca_GLOBAL_YYYYMMDD.data
top_100_ventes_<ID_MAGASIN>_YYYYMMDD-JX.data
top_100_ventes_GLOBAL_YYYYMMDD-JX.data
top_100_ca_<ID_MAGASIN>_YYYYMMDD-JX.data
top_100_ca_GLOBAL_YYYYMMDD-JX.data

Elle prend en paramètre le path parent (ou se trouve les fichiers csv a traités), la date du process, et le nombre de jour X a traité. Pour rèpondre au besoin du projet il faut mettre X a 1 puis a 7. 
Les principales mèthodes de la class ProcessData sont treatDataByMagasin et globalTreatData : 

treatDataByMagasin : cette fonction traite les données par id_magasin puis par id_produit pour calculer les 100 produits qui ont les meilleures ventes et ceux qui génèrent le plus gros Chiffre d'Affaire par magasin.Elle prend en paramètre un stream contenant les donnèes des transactions, la date du process, le path parent (ou on va gènerer les fichiers résultats), le suffixe des noms des fichiers a générer (exp -J7).

globalTreatData : cette fonction traite les données par id_produit puis par date pour calculer les 100 produits qui ont les meilleures ventes et ceux qui génèrent le plus gros Chiffre d'Affaire en général. Elle prend les mèmes paramètres que la fonction treatDataByMagasin.

Les autres fonctions readFile, getData et saveData permet de faire la lecture et l'ecriture des fichiers csv.

####GenerateData: 
Le main de cette class permet de générer des fichiers csv afin de simuler la volumétrie attendue : 
transactions_YYYYMMDD.data
reference_prod-ID_MAGASIN_YYYYMMDD.data

Elle prend en paramètre le path parent (ou on va générer les fichiers), la date de début et le nombre de jour a générer. 
Les principales méthodes sont writeTxFile et writeRefFile : 

writeTxFile : Pour créer les fichiers transactions_YYYYMMDD.data contenant les données des transactions. Chaque ligne du fichier est une transaction, elle est sous la forme txId|datetime|magasin|produit|qte .

writeRefFile : Pour créer les fichiers reference_prod-ID_MAGASIN_YYYYMMDD.data contenant les données référentiels. Une ligne du fichier est sous la forme produit|prix 

où:

txId : id de transaction (nombre)
datetime : date et heure au format ISO 8601
magasin : UUID identifiant le magasin
produit : id du produit (nombre)
qte : quantité (nombre)
prix : prix du produit en euros

La méthode getRandomMagasinId renvoi un id_magasin (exp 72a2876c-bc8b-4f35-8882-8d661fac2606) en utilisant la fonction getRandomString. getRandomString prend en paramètre la taille de la chaine de caractère a générer d'une facon aléatoire, par exemple un id_magasin une concaténation d'un ensemble de chaines séparées par le caractère "-" tout en respectant la taille de chaque chaine.

La méthode getRandomDate renvoi une date aléatoire sous la forme "yyyyMMdd HHmmssSSSZ", exemple : 20190527 000912199+0200



