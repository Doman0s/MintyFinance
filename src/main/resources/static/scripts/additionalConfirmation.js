document.addEventListener('DOMContentLoaded', function() {
    var forms = document.querySelectorAll('form[data-confirm]');
    forms.forEach(function(form) {
        var message = form.getAttribute('data-confirm');
        form.addEventListener('submit', function(event) {
            var confirmation = confirm(message);
            if (!confirmation) {
                event.preventDefault(); // Zapobieganie wysłaniu formularza
            }
        });
    });
});
