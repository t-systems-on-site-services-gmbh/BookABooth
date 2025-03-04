<template>
  <div class="home row">
    <div class="col-12 col-md-3 mb-3 mb-md-0">
      <span><img class="logo" src="https://www.jade-hs.de/typo3conf/ext/jhs_site/Resources/Public/Images/jadehs-logo.png" /></span>
      <div class="news">
        ++ Jade Karrieretag 2025 ++ <br />
        Nächster Termin: Mittwoch, 19. November 2025 <br />
      </div>
    </div>
    <div class="col-md-9">
      <div class="alert alert-success width-on-pc" v-if="authenticated">
        <span v-if="username">Sie sind als Benutzer "{{ username }}" angemeldet.</span>
      </div>
      <h1 class="display-4">Willkommen!</h1>
      <adminDashboard v-if="hasAnyAuthority('ROLE_ADMIN') && authenticated"></adminDashboard>
      <p v-if="!hasAnyAuthority('ROLE_ADMIN')" class="lead">Hier können Sie Ihren Stand für den Karrieretag buchen.</p>
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
      </div>
      <div v-if="authenticated && !hasAnyAuthority('ROLE_ADMIN') && hasAnyAuthority('ROLE_USER')">
        <div class="list-group width-on-pc">
          <userChecklist></userChecklist>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@import '/content/scss/_bootstrap-variables.scss';
.logo {
  max-width: 100%;
  height: auto;
  display: block;
  margin: auto;
}
.news {
  font-size: 11pt;
  font-weight: 600;
  margin-top: 25px;
  text-align: center;
  color: white;
  background-color: $jhs_color_red;
}
.link {
  color: #3e8acc;
  cursor: pointer;
  text-decoration: none;
  font-weight: bold;
}
.link:hover {
  cursor: pointer;
  text-decoration: underline;
  color: #286396;
}
.width-on-pc {
  @media (min-width: 768px) {
    width: 50%;
  }
}
</style>

<script lang="ts" src="./home.component.ts"></script>
