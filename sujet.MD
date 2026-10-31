
# sprint -6
creer  un API
=>dans le framework en creer un annotation WebApi lorsque on utilise cet annotation dans la methode annoter retour  (json)
annotation toJson transformer un object on json
qui ne fonction dan view mais return un json

dans frontservlet test cet annotation si il existe , il doit retourn u json ;
sinon 


## suite
upload
session
role(authorisation)

# sprint -7
-Banding du donnée arriver , envoye du donnée venant du vue vers le controller 
-Avoir une formulaire d'ajout , dans la formulaire il y aura un boutton submit vers l'url
-methode save (employe recoit argument employe) 
-clique button pour aller dans front servlet (faire savoir le save , verifie methode correspondant)
verifie parametre 
recoit parametre emp
capable de reconnaitre la variable dans requette 
on utilise celui qui n'est pas objet (string nom, int age ,.....)
si ily a objet donc frose exception, par defaut : null 
prendre la valeur et c'est celui la qu'on utilise lorsqu'on invoque 

controller cote dev 
avoir une methode save avec 3 parametre par exemple
null par defaut, prendre la nombre d'argument du methode et on le mtch apres
on l'invoquena par la valeur 

# Sprint 8 : 
input type file 
à l'arriver dans le serveur , est ce qu'on y trouve type file ?
il y a un ficher qui contient file qui arrive ,du coup on fait une formulaire et ses actions y va
formulaire ajout methode save (instance)

si dans champ formulaire type file
mila tonga ao anaty champ
on stocke dans serveur ou sa base de donnée ?
comment il arrive dans son type d'action alors ?
prend parametre 

creation objet
traitement file 
dans objet employe prend CIN type byte => on bind
avoir class employe model employe nom , .... 
employe scan cin (exemple)  => type tableau byte
ajout map dans objet 

creer class , notre classe à mettre dans son type  => mapfile attribut filename ,... tableau byte 
exemple : envoye de la recto à instance vers frameword , rrecto cin dans input recto CIN type file
s'il y a type rectoCIN dans parametre donc on creer instance de mapfile , on l'a avant request et on prend son nom apres

# Sprint 9 : session
controlleur dev :
comportement servlet , un seul instance qui repond a toutes les requette dans contexte
clé : nom du controlleur et valeur l'instance
ajout attribut dans controlleur 
parametre methode d'actions 
creer class session pour voir le parametre pour injecter
si dans methode d'action besoin de session , doit il recreer toujours ce class ? probleme memoire (il faut eviter)
class methode standard , afaka mi suppr ao anaty variable d'actions 
Map session 
pour chaque session une seule suffira , 
creer class  mysessionavec map,string, objet , methode utilitaire (pour recuperer variable )
nouveau type à creer pour mettre dans methode d'actions 
peu jouer avec tout les supp
cote framework : traintement paramettre , type session pour cree instance => map dedans à remplir a partir de httppSession ,on met dans boucle l'invoke et  on met à jour le httpsession 
on garde le nom du variable qu'on a mis le Map , et c'est sur lui qu'on clic le httpsession apres l'invoque du session
clic => on boucle,le but c'est d'eviter le httpsession direct vers le dev
pas de instance , sjuste session

# Sprint 10 
Méthode d’action 
Associer dans URL 
dans les autres methode , il faut se connecter avant de connecter avec
la personne doit Connecte authentifier pour pouvoir executer 

Annotation à mettre pour verifier si l'authentification est faite ou pas
Lorsqu'une annotation est rencontrée, le système vérifie si le nom de variable spécifié existe.
Le framework détermine la manière de gérer les variables de session, soit par le biais d'une configuration, soit en suivant une convention.
Un paramètre d'initialisation (« Init Param ») définit le nom de la variable utilisée pour suivre si un utilisateur est connecté ou non.
Ce paramètre est spécifié dans le fichier `web.xml`.
Methode init 
Servlet content 
L'utilisateur est authentifié.
Set attribut 

# Sprint 10 bis
Rôle spécifique 
Seul un rôle « prof » peut exécuter ceci.
Rôles multiples.

# Sprint-11 
avant : dans notre app de test, on avait par exemple une classe EtudiantControl'er.java contenant des méthodes dedans et quand on entre dans cette classe, notre framework créé à chaque fois une instance de cet controller quand on l'appelle, par exemple on utilise méthode1 , méthode2 ....

Objectif : rendre notre framework pour que lorsqu'il appelle un controller dans ce FrontControllerServlet , il instance UNE FOIS Seulement un contrôler et n'instance plus quand il appelle plus tard ce controller