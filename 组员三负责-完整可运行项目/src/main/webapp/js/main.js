/* 游客点击「我要发布」时弹出提示，确认后再去登录。已登录用户不拦截。 */
(function () {
    var modal = document.getElementById("guestModal");

    function openGuestModal(feature) {
        if (!modal) {
            window.location.href = window.CTX + "/user?action=toLogin";
            return;
        }
        var text = document.getElementById("guestModalText");
        var go = document.getElementById("guestGoLogin");
        text.textContent = "你为游客，不可进行「" + (feature || "该") + "」功能。";
        var from = window.location.pathname + window.location.search;
        go.href = window.CTX + "/user?action=toLogin&from=" + encodeURIComponent(from);
        modal.hidden = false;
        go.focus();
    }

    function closeGuestModal() {
        if (modal) {
            modal.hidden = true;
        }
    }

    document.addEventListener("click", function (event) {
        var authLink = event.target.closest ? event.target.closest(".js-auth") : null;
        if (authLink && !window.LOGGED_IN) {
            event.preventDefault();
            openGuestModal(authLink.getAttribute("data-feature") || "该");
            return;
        }
        if (event.target.closest && event.target.closest("[data-close]")) {
            closeGuestModal();
        }
    });

    document.addEventListener("submit", function (event) {
        var form = event.target;
        if (form.classList && form.classList.contains("js-auth-form") && !window.LOGGED_IN) {
            event.preventDefault();
            openGuestModal(form.getAttribute("data-feature") || "该");
        }
    }, true);

    var cancel = document.getElementById("guestCancel");
    if (cancel) {
        cancel.addEventListener("click", closeGuestModal);
    }
    document.addEventListener("keydown", function (event) {
        if (event.key === "Escape") {
            closeGuestModal();
        }
    });
})();
