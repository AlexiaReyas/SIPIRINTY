<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion</title>
</head>
<body>
    <h1>Connexion</h1>
    <form action="${pageContext.request.contextPath}/login" method="post">
        <label>Nom d'utilisateur <input type="text" name="username" required></label><br>
        <button type="submit">Se connecter</button>
    </form>
</body>
</html>