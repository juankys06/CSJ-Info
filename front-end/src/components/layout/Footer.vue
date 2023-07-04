<template>
  <footer id="footer">
    <div id="footerMenu" class="row">
      <div id="footer_navigation" class="sort-pages modify-pages">
        <ul>
          <li class="">
            <a
              href="https://www.ramajudicial.gov.co/web/informacion/cuentas-de-correo-para-notificaciones"
              ><span>Cuentas de correo para Notificaciones Judiciales</span></a
            >
          </li>
          <li class="">
            <a
              href="https://www.ramajudicial.gov.co/portal/politicas-de-privacidad-y-condiciones-de-uso"
              ><span>Politicas de Privacidad y Condiciones de Uso</span></a
            >
          </li>
          <li class="">
            <a
              href="https://login.microsoftonline.com/login.srf?wa=wsignin1.0&rpsnv=3&ct=1402516770&rver=6.4.6456.0&wp=MCMBI&wreply=https:%2F%2Fportal.office.com%2Flanding.aspx%3Ftarget%3D%252fdefault.aspx&lc=3082&id=501392"
              target="_blank"
              ><span>Correo Institucional</span></a
            >
          </li>
        </ul>
      </div>
    </div>

    <div id="rama-footer" class="row">
      <div class="col-lg-4 col-md-4 col-xs-12 block-redes">
        <strong>Síganos</strong>
        <p></p>
        <ul id="footer-social-link" class="">
          <li class="facebook">
            <a href="https://www.facebook.com/judicaturacol/"
              ><i class="fa fa-facebook-f"></i
            ></a>
          </li>
          <li class="twitter">
            <a href="http://www.twitter.com/judicaturacsj"
              ><i class="fa fa-twitter"></i
            ></a>
          </li>
          <li class="youtube">
            <a href="https://www.youtube.com/channel/UCBHrdbfpLE2lNsu4CTpDanA"
              ><i class="fa fa-youtube"></i
            ></a>
          </li>
          <li class="email">
            <a href="mailto:info@cendoj.ramajudicial.gov.co"
              ><i class="fa fa-envelope"></i
            ></a>
          </li>
        </ul>
      </div>
      <div class="col-lg-4 col-md-4 col-xs-12">
        <i class="fas fa-band-aid"></i>
        <h3 style="text-align: center">
          <span style="color: #ffffff">Rama Judicial </span>
        </h3>
        <p style="text-align: center">
          <span
            >Calle 12 No. 7 - 65 Bogotá Colombia<br />
            PBX: (571) 565 8500 - E-mail: info@cendoj.ramajudicial.gov.co<br />
            Acceder al Directorio de Despachos Judiciales</span
          >
        </p>
        <p style="text-align: center">
          <span
            >Horario de Atención Lunes a Viernes<br />
            8:00 a.m. a 1:00 p.m. - 2:00 p.m. a 5:00 p.m.
          </span>
        </p>
      </div>
    </div>
    <div id="reporte-visitas" class="row"></div>
    <div id="accesibilityContent" class="aui-helper-hidden"></div>
  </footer>
</template>

<script>
export default {
  data() {
    return {
      date: "",
      meses: [
        "Enero",
        "Febrero",
        "Marzo",
        "Abril",
        "Mayo",
        "Junio",
        "Julio",
        "Agosto",
        "Septiembre",
        "Octubre",
        "Noviembre",
        "Diciembre",
      ],
    };
  },
  mounted() {
    this.date = new Date();
  },
};
</script>

<style>
/* ---------- Footer ---------- */
#footer {
  color: #fff;
  width: 100%;
  background: #19325b url("../../assets/bg-footer.png") no-repeat 0 0;
  background-position: center;
  overflow: auto;
  padding-top: 20px;
  background-size: 85%;
}

#footer .cont h4 {
  color: #fff;
}

#footer_social_link {
  margin-top: 20px;
}

#footerMenu,
#footer-social-link,
#rama-footer,
#reporte-visitas {
  margin-bottom: 20px;
  margin-left: 0px;
  margin-right: 0px;
}

footer .container p,
footer .container span,
footer .container b {
  color: #fff;
}
#footer_navigation {
  width: 100%;
}
#footer_navigation li {
  text-align: center;
  padding: 0px 5% 0px 5%;
}

#footer_navigation li + li {
  border-left: 1px solid #3c91a5;
}

#footer_navigation a,
#footer_navigation span {
  color: #65c0e7;
}

#footer_navigation a:hover span,
#footer_navigation a:hover {
  color: #fff;
  text-decoration: none;
}

/* ---------- Entidades ---------- */

#logos_entidades {
  margin-bottom: 20px;
}

#entidades .item-entidades {
  padding-left: 10px;
  padding-right: 10px;
  display: inline-block;
}

#entidades .item-entidades img {
  opacity: 0.4;
  filter: grayscale(100%);
}

#entidades .item-entidades a:hover img {
  opacity: 1;
  transform: scale(1.2);
  filter: grayscale(0%);
  transition: transform 1s, opacity 1.5s, filter 1.5s;
}

#entidades .item-entidades a img {
  max-height: 90px;
  max-width: 90px;
}
/* ---------- Social Icons ---------- */
#footer-social-link {
  width: auto;
  margin: 0 auto;
  margin-left: 15px;
  list-style-type: none;
}

#footer-social-link a {
  font-size: 2em;
  color: #fff;
}

#footer-social-link a:hover {
  text-decoration: none;
}

#reporte-visita,
#footer-social-link,
#rama-footer {
  margin-bottom: 20px;
}

#footer-social-link li {
  float: left;
  margin-right: 15%;
}

.block-visitas {
  text-align: right;
}

.title-report {
  color: #65c0e7;
}

#rama-footer p {
  margin: 0 0 10px !important;
}

.col-xs-10 {
  width: 100%;
}
</style>
