
const btns = document.querySelectorAll(".dropdown-btn");

btns.forEach(btn => {
    btn.addEventListener("click", (e) => {

        e.stopPropagation(); // evita fechar imediatamente

        const submenu = btn.nextElementSibling;
        const arrow = btn.querySelector(".arrow");

        // Fecha os outros
        document.querySelectorAll(".submenu").forEach(menu => {
            if (menu !== submenu) menu.classList.remove("open");
        });

        document.querySelectorAll(".arrow").forEach(a => {
            if (a !== arrow) a.classList.remove("rotate");
        });

        // Alterna atual
        submenu.classList.toggle("open");
        arrow.classList.toggle("rotate");
    });
});



document.addEventListener("click", () => {

    document.querySelectorAll(".submenu").forEach(menu => {
        menu.classList.remove("open");
    });

    document.querySelectorAll(".arrow").forEach(a => {
        a.classList.remove("rotate");
    });

});

const toggle = document.querySelector(".btn_menu");
const nav = document.querySelector(".nav");


toggle.addEventListener("click", () => {
    nav.classList.toggle("active");
});



const form = document.getElementById("form_veic");

form.addEventListener("submit", function(event) {
    event.preventDefault();

    const marca = document.getElementById("txf_marca_add_veic").value.trim();
    const modelo = document.getElementById("txf_modelo_add_veic").value.trim();
    const combustivel = document.getElementById("ddl_combustivel_add_veic").value;
    const placa = document.getElementById("txf_placa_add_veic").value.trim();
    const km = document.getElementById("txf_km_add_veic").value.trim();
    const prefixo = document.getElementById("txf_prefixo_add_veic").value.trim();
    const nucleo = document.getElementById("txf_nucleo_add_veic").value.trim();
    const numeroFi = document.getElementById("txf_nfi_add_veic").value.trim();

    if (
        marca === "" ||
        modelo === "" ||
        placa === "" ||
        km === "" ||
        prefixo === "" ||
        nucleo === "" ||
        numeroFi === ""
    ) {
        alert("Preencha todos os campos!");
        return;
    }

    if (combustivel === "") {
        alert("Selecione o tipo de combustível!");
        return;
    }
    console.log("Validação OK");

    const dados = {
        marca,
        modelo,
        combustivel,
        placa,
        km,
        prefixo,
        nucleo,
        numeroFi
    };

    console.log("Dados enviados", dados);

        const API_URL = "URL_DO_BACKEND_AQUI";

        fetch(API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(dados)
    });

});


