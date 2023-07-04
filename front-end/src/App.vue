<template>
  <div id="app">
    <Header />
    <br />
    <br />
    <router-view />
    <br />
    <br />
    <Footer />
  </div>
</template>

<script>
// @ is an alias to /src
import Footer from "@/components/layout/Footer.vue";
import Header from "@/components/layout/Header.vue";

export default {
  name: "Home",
  components: {
    Header,
    Footer,
  },
};
</script>

<style>
:root {
  --ck-z-default: 100 !important;
  --ck-z-modal: calc(var(--ck-z-default) + 999);
}

.modify-pages li {
  position: relative;
}

.modify-pages .delete-tab {
  cursor: pointer;
  display: block;
  height: 8px;
  margin-top: -7px;
  padding: 3px;
  position: absolute;
  right: 1px;
  text-indent: -9999em;
  top: 50%;
  width: 8px;
}

/*------ Navigation portlet ------*/

.nav-menu ul {
  padding-left: 10px;
}

.nav-menu li a.ancestor {
  font-weight: bold;
}

.nav-menu li a.selected {
  color: #000;
  font-weight: bold;
  text-decoration: none;
}

/* ---------- Page template selector ---------- */

.aui-form-options {
  border: 1px solid #aaa;
  display: inline-block;
  vertical-align: middle;
}

.aui-form-options input {
  border-width: 0;
  display: inline;
  float: left;
}

.aui-form-triggers {
  float: left;
}

/* ---------- Navigation Top ---------- */
#navigation {
  background-color: #19325b;
  clear: both;
  margin: 0 auto 5px;
  min-height: 2.2em;
  padding: 0 5px;
  position: relative;
}

#navigation > ul {
  margin: 0 auto;
}

#navigation li {
  position: relative;
  vertical-align: top;
}

#navigation a {
  color: #000;
  font-size: 1.1em;
  font-weight: bold;
  margin: 0 1px;
  padding: 3px 15px;
  text-decoration: none;
}

#navigation .selected a,
#navigation li.hover a,
#navigation a:hover,
#navigation a:focus,
#navigation .child-menu li a,
#navigation .yui3-dd-dragging,
.lfr-navigation-proxy {
  background: #3c91a5;
  color: #fff;
  transition: all 0.5s;
}

#navigation li.hover .child-menu {
  display: block;
  position: absolute;
  z-index: 250;
}

#navigation .child-menu {
  display: none;
}

#navigation .child-menu .portlet-column.portlet-column.portlet-column {
  display: table-cell;
  vertical-align: top;
}

#navigation .child-menu li {
  border-width: 0 0 1px;
  display: block;
  float: none;
  margin: 0;
}

#navigation .child-menu li a {
  cursor: pointer !important;
}

#navigation li .child-menu {
  background: none repeat scroll 0 0 #ffffff;
  height: auto;
  width: 100%;
  padding: 0px;
  margin: 0px;
}

#navigation li.hover div.child-menu {
  height: auto;
  width: auto;
}

#navigation .child-menu {
  display: none;
}

#navigation .child-menu li {
  background: none repeat scroll 0 0;
  border-width: 0 0 1px;
  display: block;
  float: none;
  margin: 0;
}

#navigation .child-menu li a {
  cursor: pointer !important;
}

#navigation,
#top_navigation,
#footer_navigation {
  clear: both;
  margin: 0;
  padding: 0;
  position: relative;
}

#navigation ul,
#top_navigation ul,
#footer_navigation ul {
  list-style: none;
}

#navigation ul {
  list-style: none;
  background-color: #19325b;
}

.dropdown-item a,
.dropdown-submenu a {
  font-size: 11px !important;
}

#superiorMenu #top_navigation ul {
  width: 100%;
  font-size: 0.688em;
  position: relative;
  margin: 0 auto;
}

#superiorMenu #top_navigation ul li.languageSelection {
  position: absolute;
  top: 0px;
  right: 0px;
  padding: 5px 0px;
  background: none repeat scroll 0 0;
}

#footerMenu #footer_navigation {
  background: none repeat scroll 0 0;
  text-align: center;
  height: 35px;
}

#footerMenu #footer_navigation ul {
  height: 35px;
}

#navigation li,
#navigation a,
#top_navigation li,
#top_navigation a,
#footer_navigation li,
#footer_navigation a {
  display: inline-block;
  text-decoration: none;
}

#navigation li,
#top_navigation li,
#footer_navigation li {
  position: relative;
  vertical-align: top;
  margin: 0px 0px;
}

#footerMenu #footer_navigation li {
  background: none repeat scroll 0 0;
}

#navigation a {
  font-size: 1em;
  color: #fff;
  padding: 7px 11px;
  text-decoration: none;
  text-transform: uppercase;
}

#top_navigation,
#logos_entidades {
  display: table;
  margin: 0 auto;
  text-align: center;
}

#top_navigation ul .yellow {
  background: #ffd400;
  color: #000;
}

#top_navigation ul .blue {
  background: #0056b1;
}

#top_navigation ul .red {
  background: #ff0218;
}

#top_navigation ul .green {
  background: #00cab0;
}

@media only screen and (min-width: 800px) {
  #top_navigation,
  #top_navigation ul li a {
    width: 100%;
  }

  #top_navigation ul li {
    width: 25%;
  }
}

#top_navigation ul li a {
  font-weight: bold;
  position: relative;
  color: #fff;
  overflow: hidden;
  height: 40px;
  font-size: 15px;
}

@media only screen and (max-width: 799px) {
  #top_navigation ul li a {
    width: 220px;
  }
}

#top_navigation ul li a:before {
  position: absolute;
  top: 0px;
  left: 0px;
  z-index: 1;
  padding: 10px 20px;
  background: none repeat scroll 0% 0% #fff;
  color: black;
  content: attr(title);
  transition: transform 1s ease 0s;
  height: 40px;
  width: 100%;
}

#top_navigation ul li a span {
  display: block;
  position: relative;
  padding: 8px 0px;
  background: none repeat scroll 0% 0% #0f7c67;
  transition: transform 1s ease 0s;
  z-index: 2;
  height: 40px;
}

#top_navigation ul li a:hover {
  text-decoration: none;
}

#top_navigation ul li a:hover span {
  transform: translatex(100%);
  width: 100%;
  height: 40px;
}

#footerMenu #footer_navigation a {
  text-transform: none;
  padding: 10px;
}

#footerMenu #footer_navigation span.lastUpdateDate {
  display: inline-block;
  font-size: 0.75em;
  padding: 10px;
}

#navigation a span img,
#top_navigation a span img,
#footer_navigation a span img {
  float: left;
  padding-right: 5px;
}

#navigation li.last > a {
  padding: 7px 25px;
}

#navigation .yui3-dd-drop a,
#top_navigation .yui3-dd-drop a,
#footer_navigation .yui3-dd-drop a {
  cursor: move;
}

#footerMenu #footer_navigation .selected a,
#footerMenu #footer_navigation li.hover a,
#footerMenu #footer_navigation a:hover,
#footerMenu #footer_navigation a:focus,
#footerMenu #footer_navigation .child-menu li a,
#footerMenu #footer_navigation .yui3-dd-dragging,
#footerMenu .lfr-navigation-proxy {
  background: none repeat scroll 0 0;
}

.red {
  color: black;
}

#login {
  background-color: #fff;
  width: 100%;
  position: relative;
  height: auto;
  overflow: hidden;
}

#loginnav,
#loginnav button {
  font-size: 12px !important;
}

.navbar-nav li a {
  padding-top: 5px;
  padding-bottom: 5px;
}

#loginnav .navbar-btn {
  margin-top: 0;
  margin-bottom: 0;
}

/* ////////////////////////////////////// */

body {
  /* padding-top: 50px; */
  padding-bottom: 20px;
  background-color: white;
}

/* Wrapping element */
/* Set some basic padding to keep content from hitting the edges */

/* Carousel */
.carousel-caption p {
  font-size: 20px;
  line-height: 1.4;
}

/* Make .svg files in the carousel display properly in older browsers */
.carousel-inner .item img[src$=".svg"] {
  width: 100%;
}

/* QR code generator */
#qrCode {
  margin: 15px;
}

/* Hide/rearrange for smaller screens */
@media screen and (max-width: 767px) {
  /* Hide captions */
  .carousel-caption {
    display: none;
  }
}

/* 
    *****************
    My Custom Classes
    *****************
*/

.process-section h3 {
  position: relative;
  margin: 1.5rem 0 1.5rem 0;
}

#search_process input[name="codProceso"],
#search_process input[name="ciudad"] {
  width: 3.4em;
}

#search_process input[name="entidad"],
#search_process input[name="especialidad"],
#search_process input[name="numero"] {
  width: 2em;
}

#search_process input[name="despacho"] {
  width: 2.5em;
}

#search_process input[name="año"] {
  width: 3em;
}

#search_process input[name="año"],
#search_process input[name="codProceso"] {
  background-color: rgb(252, 243, 135);
}

#search_process table td input[type="text"] {
  width: 70%;
}

input[name="tasa_pacto_a"],
input[name="tasa_pacto_b"] {
  display: inline-block;
}

.no-numbar {
  -moz-appearance: textfield;
  -webkit-appearance: textfield;
  appearance: textfield;
}

.oculto {
  display: none;
}

#descargarExcel span,
#descargarPdf span {
  font-size: 1.5em;
  text-align: center;
}

.navbar-inverse .btn-link {
  color: white;
}

form > div > .btn {
  margin-top: 10px;
  margin-bottom: 10px;
  width: 40%;
}

.dropdown-submenu {
  position: relative;
}

.dropdown-submenu .dropdown-menu {
  top: 0;
  left: 100%;
  margin-top: -1px;
}

.dropdown-submenu:hover .dropdown-menu {
  display: block;
}

div.about-div {
  width: 430px;
}

div.about-div textarea {
  resize: none;
  width: 100%;
  overflow: hidden;
  text-align: left;
}

.navbar-inverse {
  background-color: #0f559c;
  border-color: #0f559c;
}

.navbar-inverse .navbar-brand {
  color: #fff;
}

.navbar-inverse .navbar-nav > li > a {
  color: #fff;
}

h1,
h2 {
  color: #0f559c;
}

h3,
h4 {
  color: #026dca;
}

legend {
  border-bottom: none;
}

.form-control.form-control.form-control {
  border: 1px solid;
  border-color: #bfbfbf #dedede #dedede #bfbfbf;
  font: 1em Arial, Helvetica, Verdana, sans-serif;
  padding: 5px 1px;
}

.btn-default {
  background: #d4d4d4;
  border: 1px solid;
  border-color: #c8c9ca #9e9e9e #9e9e9e #c8c9ca;
  color: #34404f;
  font-weight: bold;
  overflow: visible;
  text-shadow: 1px 1px #fff;
}

.btn-default:hover {
  background: #b9ced9;
  border-color: #627782;
  color: #369;
}

.btn-default:focus {
  background: #ebebeb;
  border-color: #555;
}

.navbar-inverse .navbar-nav > .open > a,
.navbar-inverse .navbar-nav > .open > a:hover,
.navbar-inverse .navbar-nav > .open > a:focus {
  background-color: #026dca;
}

.block-title-fieldset {
  border: 1px 0 solid #f7f7f7;
  border-radius: 5px;
  width: auto;
}

#tbl_he input {
  /* Tabla de horas extras en calculadora salarial */
  width: 2em;
}

.cuadro-tasas {
  margin-top: 50%;
  background-color: #eee;
}

table.dataTable.display tbody tr.highlight {
  background-color: #ffcfcf;
}

fieldset {
  display: block;
  margin-left: 2px;
  margin-right: 2px;
  padding-top: 0.35em;
  padding-bottom: 0.625em;
  padding-left: 0.75em;
  padding-right: 0.75em;
  /* border: 1px solid #dddddd; */
}

.tbl-parm-reliq {
  width: 100%;
  padding-left: 4%;
  border-collapse: separate;
  border-spacing: 0px 1rem;
}

.formulario-auditoria {
  margin: 4rem 0 2rem 0;
}

@media (min-width: 768px) {
  .navbar-right {
    margin-right: inherit;
  }
}

tfoot {
  font-size: 12px;
}

.g-recaptcha {
  display: inline-block;
}

@media (min-width: 1200px) {
  #content {
    width: 1370px;
  }
}

.btn-custom {
  background-color: #19325b;
  color: white;
}

.btn-custom:hover {
  transform: scale(1.1);
  color: white;
}

.fecha-tasas {
  font-size: 0.7em;
}

/* //////////////////////////////////// */
.logo img {
  width: 120px;
  height: auto;
}

.logo img {
  width: 120px;
  height: auto;
}

.block-margin-img1 {
  margin-top: 12%;
}

.site-title-content #searchBox h1 {
  color: #19325b;
  font-weight: 600;
}

.site-title-content #searchBox h1 {
  color: #19325b;
  font-weight: 600;
}

#banner {
  margin-top: 0px;
  position: relative;
}

#banner {
  margin-top: 0px;
  position: relative;
}

body,
div,
dl,
dt,
dd,
ul,
ol,
li,
h1,
h2,
h3,
h4,
h5,
h6,
pre,
form,
fieldset,
input,
textarea,
p,
blockquote,
th,
td {
  margin: 0;
  padding: 0;
}

.box-logo,
.box-logo2 {
  display: table;
  margin: 0 auto;
}

.site-title-content {
  text-align: center;
  margin-bottom: 20px;
}

#heading .currentDate {
  /* color: #3c91a5; */
  color: #337ab7;
  text-transform: capitalize;
  font-size: 18px;

  display: block;
}

#searchBox .icons-search {
  text-align: center;
  /* font-size: 2.3em; */
  padding: 0px;
}

.swal2-title {
  font-size: 20px !important;
}
.swal2-content {
  font-size: 14px !important;
}
.swal2-icon {
  margin-bottom: 10px !important;
  margin-top: 0px !important;
}
.swal2-styled.swal2-confirm,
.swal2-styled.swal2-cancel {
  font-size: 14px !important;
}
</style>
