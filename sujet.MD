
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
rehefa tong any anaty serveur de misy type hoe file ve ? 
misy fichier misy file tonga ao 
manao formulaire de le actions any le formumaire zany makany 
formulaire ajout methode save 
mitontona ao am save de manana instance de atreo loaa

raha amny champ anah formulaire misy type file 
mila tonga ao anaty champ
stockena any anaty serveur ve sa base de donnée 
aona ny atongavany ao am type d'actions any zany 

mandray parametre 

manamboatra objet (okey)
traitement anah file 
ao anaty objet employe mandray CIN type byte => de bindena 
manana class employe model employe nom , .... 
employe scan cin ohatra  => type tableau byte
asiana map no mety ao anaty objet 

micreer class de le classe tsika no atao type any  => mapfile attribut filename ,...  de le tableau anah byte 
ohatra andefa any le recto any le zavatra de instancena aty amny frameword , recto cin ao anaty input rectoCIN type file
raha misy type rectoCIN misy amny parametre de micreer instance anah mapfile de azo alohany request de alaiko ny anarany aveo

# Sprint 9 : session
ataovy izay ahatonga any le session any le utilisateur disponible ao anaty controlleur any le developpeur
controlleur dev :
comportement servlet , instance ray indrayy iany de in no mamaly requette retraretra , ao anaty contexte 
clé anarany le controlleur de valeur le instance 
de mijery hoe misy na tsia 
manisy attribut ao anaty controlleur 
parametre methode d'actions foana 
manaboatra class session atsika mahita aniny le parametre atsika de injectena 
raha ohatra ve isakiny methode d'actions izay mila session , de icree anio class io foana ve izy ? problem anah mémoire (il faut éviter)
class methode standard , afaka mi suppr ao anaty variable d'actions 
Map session 
pour chaque session ray iany , 
creer class ray mysession de misy map,string, objet , asiana methode utilitaire (micrecuperer variable )
type vaovao creen tsiak in apetraka aoa anaty methode d'actions 
de afaka milalao supp,retraretra 
cote framework : traintement paramettre , type session antsiak de micree instance => map ao anatiny fenoina a partir httppSession , boucle de apetraka ao
vita ny invoke de mila atao à jour le httpsession 
tazonmina ny anarany le variable nasina any le Map de in aveo no tsindrina amny in le httpsession rhf vita le invoke any le session 
tsindrina => bouclena fotsiny loa,tanjona mba tsy misy httppSession direct be any amny le développeur ,
le session loa no atao fa mbola tsy atao le instance loa 
de jerena ny coté performance am larobia
Vous avez envoyé

# Sprint 10 
Méthode d’action 
Associer ao anaty URL 
Ao anaty le methode reny tsy maintsy connecter vo afaka connecter amny 
Connecte authentifier le olona vo afaka m’executer 

Annotation tsy maintsy atao mipetraka eo de miverifier hoe authentifier eny sa tsia 
Isakiny mahita ny annotation de verifiena hoe n’existe ce ty anarana variable 
Aona ny alalany framework hoe in variable de session
Configuration na convention
Init Param de ty ny anarany variable a lalana hoe connecter na tsia 
Omena ny Param ao anaty web.xml
Methode init 
Servlet content 
Authentifié ty olona ty 
Set attribut 

# Sprint 10 bis

Rôle spécifique 
Rôle prof no afaka m’exécuter anty 
Rôle maromaro

# Sprint-11 
avant : dans notre app de test, on avait par exemple une classe EtudiantControl'er.java contenant des méthodes dedans et quand on entre dans cette classe, notre framework créé à chaque fois une instance de cet controller quand on l'appelle, par exemple on utilise méthode1 , méthode2 ....


Objectif : rendre notre framework pour que lorsqu'il appelle un controller dans ce FrontControllerServlet , il instance UNE FOIS Seulement un contrôler et n'instance plus quand il appelle plus tard ce controller