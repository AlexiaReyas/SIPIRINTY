# Sprint 9 - Session et contrôleurs persistants

Le framework conserve une seule instance de chaque contrôleur dans le contexte
du servlet. Il injecte aussi `framework.util.MySession` dans les méthodes d'action.
Cette classe masque `HttpSession` au développeur et expose les opérations `get`,
`set`, `remove`, `contains` et `clear`.

Les valeurs sont chargées depuis la session HTTP avant l'invocation, puis le
contenu de `MySession` est resynchronisé après l'action.

