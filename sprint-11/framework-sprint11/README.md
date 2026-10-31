# Sprint 11 - Instance unique des contrôleurs

Le framework conserve une seule instance de chaque contrôleur dans le contexte
du servlet. Il injecte aussi `framework.util.MySession` dans les méthodes d'action.
Cette classe masque `HttpSession` au développeur et expose les opérations `get`,
`set`, `remove`, `contains` et `clear`.

Les valeurs sont chargées depuis la session HTTP avant l'invocation, puis le
contenu de `MySession` est resynchronisé après l'action.

Une action peut être protégée avec `@Authenticated`. Le framework vérifie alors
la présence de la variable configurée par `auth.session.attribute` dans `web.xml`.
Le nom indiqué dans `@Authenticated("nom")` est prioritaire sur cette
configuration. Une requête non authentifiée reçoit une réponse HTTP 401.

Une action peut exiger un rôle avec `@RoleAllowed("prof")` ou plusieurs rôles
avec `@RoleAllowed({"prof", "admin"})`. Un seul rôle correspondant suffit.
Le nom de l'attribut contenant les rôles est défini par
`auth.session.roles.attribute` dans `web.xml`. Un utilisateur connecté mais sans
rôle autorisé reçoit une réponse HTTP 403.

Le `FrontController` conserve les instances dans une map du `ServletContext`,
avec la classe du contrôleur comme clé. Une instance est créée uniquement lors
du premier appel à une classe, puis réutilisée pour toutes ses méthodes et les
requêtes suivantes. Le contrôleur doit donc rester sans état de requête; les
données propres à un utilisateur doivent être placées dans `MySession`.

