# Sprint 10 - Authentification par session

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

