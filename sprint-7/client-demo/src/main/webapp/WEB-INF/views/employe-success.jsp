<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Employé enregistré</title>
</head>
<body>
    <h1>Employé enregistré avec succès</h1>
    <p>Nom : ${nom}</p>
    <p>Age : ${age}</p>
    <p>Salaire : ${salaire}</p>
    <a href="${pageContext.request.contextPath}/employe/form">Ajouter un autre employé</a>
</body>
</html>