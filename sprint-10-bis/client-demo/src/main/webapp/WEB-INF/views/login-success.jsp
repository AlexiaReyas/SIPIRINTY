<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion réussie</title>
</head>
<body>
    <h1>Connexion réussie</h1>
    <p>Utilisateur connecté : ${username}</p>
    <p>Rôle : ${role}</p>
    <a href="${pageContext.request.contextPath}/employe/form">Ajouter un employé</a>
</body>
</html>