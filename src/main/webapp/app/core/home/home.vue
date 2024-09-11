<template>
  <div class="home row">
    <div class="col-md-3">
      <span><img class="logo" src="https://www.jade-hs.de/typo3conf/ext/jhs_site/Resources/Public/Images/jadehs-logo.png" /></span>
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
</style>

<script lang="ts" src="./home.component.ts"></script>
