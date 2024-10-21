<template>
  <div class="home row">
    <div class="col-md-3">
      <span><img class="logo" src="https://www.jade-hs.de/typo3conf/ext/jhs_site/Resources/Public/Images/jadehs-logo.png" /></span>
      <div class="news">
        ++ Jade Karrieretag 2024 ++ <br />
        Nächster Termin: Mittwoch, 20. November 2024 <br />
      </div>
    </div>
    <div class="col-md-9">
      <h1 class="display-4">Willkommen!</h1>
      <p class="lead">Hier können Sie Ihren Stand für den Karrieretag buchen.</p>
      <div v-if="!authenticated">
        <div>
          Der Jade Karrieretag steht wieder an und wie jedes Jahr können Sie die Gelegenheit nutzen, Aussteller auf unserem Karrieremarkt zu
          werden! Dies bietet Ihnen die Möglichkeit Ihr Unternehmen zu präsentieren und zahlreiche Gespräche mit zukünftigen
          Nachwuchskräften zu führen.
        </div>
        <br />
        <div>Um einen unserer 90 Stellplätze zu buchen, geht es hier zur <a class="alert-link" v-on:click="openLogin()">Anmeldung</a>.</div>
        <br />
        <div>
          Falls Sie noch keinen Zugang haben, können Sie sich hier
          <router-link class="alert-link" to="/register">registrieren</router-link>.
        </div>
        <div class="alert alert-success" v-if="authenticated">
          <span v-if="username">Sie sind als Benutzer "{{ username }}" angemeldet.</span>
        </div>
      </div>
      <div v-if="!hasAnyAuthority('ROLE_ADMIN') && authenticated">
        <div>
          <h4 class="mb-2 mt-4">Checkliste und Status:</h4>
          <div class="list-group w-50">
            <a href="#" class="list-group-item list-group-item-action list-group-item-success">
              <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">Registrierung</h5>
                <small>erledigt</small>
              </div>
              <p class="mb-1 font-weight-normal">Account wurde erstellt.</p>
            </a>
            <a href="#" class="list-group-item list-group-item-action list-group-item-danger">
              <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">Verifizierung</h5>
                <small class="text-body-secondary">offen</small>
              </div>
              <p class="mb-1 font-weight-normal">Bestätigen Sie Ihre E-Mail-Adresse.</p>
            </a>
            <a href="#" class="list-group-item list-group-item-action list-group-item-warning">
              <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">Kontaktdaten vervollständigt</h5>
                <small class="text-body-secondary">offen</small>
              </div>
              <p class="list-group-item list-group-item-danger mb-1 font-weight-normal">Rechnungsanschrift</p>
              <p class="list-group-item list-group-item-warning mb-1 font-weight-normal">Logo des Unternehmens</p>
              <p class="list-group-item list-group-item-warning mb-1 font-weight-normal">Pressekontakt</p>
              <p class="list-group-item list-group-item-warning mb-1 font-weight-normal">Firmenkurzbeschreibung</p>
            </a>
            <a href="#" class="list-group-item list-group-item-action list-group-item-danger">
              <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">Buchung getätigt</h5>
                <small class="text-body-secondary">offen</small>
              </div>
              <p class="mb-1 font-weight-normal"></p>
            </a>
          </div>
          <p class="mt-4">
            <small class="mr-2" style="background-color: #c3e6cb">Grün: erledigt</small>
            <small class="mr-2" style="background-color: #f5c6cb">Rot: ausstehende Pflichtangabe</small>
            <small class="mr-2" style="background-color: #ffeeba">Gelb: ausstehend, aber optional</small>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<style>
.logo {
  max-width: 100%;
  height: auto;
  display: block;
  margin: auto;
}
.news {
  font-size: 11pt;
  font-weight: 600;
  margin-top: 50px;
  margin-left: 20px;
}
</style>

<script lang="ts" src="./home.component.ts"></script>
