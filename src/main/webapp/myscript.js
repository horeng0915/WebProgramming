function openDuplicateCheckPage() {
    window.open("/checkDuplicate", "_blank", "width=600,height=400");
}

function validatePassword() {
    var passwd = document.getElementById("passwd").value;
    var passwd2 = document.getElementById("passwd2").value;
    var messageElement = document.getElementById("password-message");

    if (passwd !== passwd2) {
        messageElement.textContent = "패스워드가 일치하지 않습니다.";
        return false;
    } else {
        messageElement.textContent = "";
        return true;
    }
}