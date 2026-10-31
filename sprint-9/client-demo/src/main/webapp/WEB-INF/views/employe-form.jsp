<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Ajouter un employé</title>
</head>
<body>
    <h1>Ajouter un employé</h1>
    <form action="${pageContext.request.contextPath}/employe/save" method="post" enctype="multipart/form-data">
        <label>Nom <input type="text" name="nom" required></label><br>
        <label>Age <input type="number" name="age" required></label><br>
        <label>Salaire <input type="number" name="salaire" step="0.01" required></label><br>
        <label>Recto CIN <input type="file" name="rectoCIN" required></label><br>
        <button type="submit">Enregistrer</button>
    </form>
</body>
</html>