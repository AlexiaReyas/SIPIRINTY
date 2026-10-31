# Sprint 8 - Upload de fichiers

Le framework accepte les formulaires `multipart/form-data` et injecte un fichier
dans une methode d'action sous forme de `framework.util.MapFile`, de `byte[]` ou
de `javax.servlet.http.Part`.

Dans un objet de formulaire, le nom du champ fichier doit correspondre au nom de
la propriete Java. `MapFile` conserve le nom, le type MIME et le contenu du
fichier en memoire.

