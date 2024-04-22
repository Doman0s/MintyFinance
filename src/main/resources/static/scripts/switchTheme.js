function switchTheme() {
    let container = document.querySelector('.my-container');
    let moonIcon = document.getElementById('moonIcon');
    let sunIcon = document.getElementById('sunIcon');

    if (window.getComputedStyle(container).backgroundColor === 'rgb(66, 66, 66)') {
        container.style.backgroundColor = 'rgb(48, 48, 48)';
        moonIcon.style.display = 'inline';
        sunIcon.style.display = 'none';
        localStorage.setItem('theme', 'dark');
    } else {
        container.style.backgroundColor = 'rgb(66, 66, 66)';
        moonIcon.style.display = 'none';
        sunIcon.style.display = 'inline';
        localStorage.setItem('theme', 'light');
    }
}

function setInitialTheme() {
    let storedTheme = localStorage.getItem('theme');
    let container = document.querySelector('.my-container');
    let moonIcon = document.getElementById('moonIcon');
    let sunIcon = document.getElementById('sunIcon');

    if (storedTheme === 'dark') {
        container.style.backgroundColor = 'rgb(48, 48, 48)';
        moonIcon.style.display = 'inline';
        sunIcon.style.display = 'none';
    } else {
        container.style.backgroundColor = 'rgb(66, 66, 66)';
        moonIcon.style.display = 'none';
        sunIcon.style.display = 'inline';
    }
}

window.onload = setInitialTheme;