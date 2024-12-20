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

          <div v-if="verified === false" class="d-flex justify-content-center my-2">
            <div class="spinner-border" role="status">
              <span class="sr-only">Loading...</span>
            </div>
          </div>

          <div v-if="verified === true" class="list-group w-50">
            <div
              v-bind:class="{
                'mb-0': true,
                'list-group-item': true,
                'list-group-item-action': true,
                'list-group-item-success': authenticated,
                'list-group-item-danger': !authenticated,
              }"
            >
              <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">Registrierung</h5>
                <small class="text-body-secondary" v-text="authenticated ? 'erledigt' : 'offen'"></small>
              </div>
              <p class="mb-1 font-weight-normal">Account wurde erstellt.</p>
            </div>
            <div
              v-bind:class="{
                'mb-0': true,
                'list-group-item': true,
                'list-group-item-action': true,
                'list-group-item-success': verified,
                'list-group-item-danger': !verified,
              }"
            >
              <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">Verifizierung</h5>
                <small class="text-body-secondary" v-text="verified ? 'erledigt' : 'offen'"></small>
              </div>
              <p class="mb-1 font-weight-normal">Account ist verifiziert.</p>
            </div>
            <div
              v-bind:class="{
                'mb-0': true,
                'list-group-item': true,
                'list-group-item-action': true,
                'list-group-item-success': address && logo && pressContact && companyDescription,
                'list-group-item-warning': !(address && logo && pressContact && companyDescription),
              }"
            >
              <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">Kontaktdaten</h5>
                <small
                  class="text-body-secondary"
                  v-text="address && logo && pressContact && companyDescription ? 'erledigt' : 'offen'"
                ></small>
              </div>
              <div
                :class="{
                  'list-group-item': true,
                  'list-group-item-danger': !address,
                  'list-group-item-success': address,
                  'mb-0': true,
                  'font-weight-normal': true,
                }"
              >
                Rechnungsanschrift
              </div>
              <div
                :class="{
                  'list-group-item': true,
                  'list-group-item-warning': !logo,
                  'list-group-item-success': logo,
                  'mb-0': true,
                  'font-weight-normal': true,
                }"
              >
                Logo des Unternehmens
              </div>
              <div
                :class="{
                  'list-group-item': true,
                  'list-group-item-warning': !pressContact,
                  'list-group-item-success': pressContact,
                  'mb-0': true,
                  'font-weight-normal': true,
                }"
              >
                Pressekontakt
              </div>
              <div
                :class="{
                  'list-group-item': true,
                  'list-group-item-warning': !companyDescription,
                  'list-group-item-success': companyDescription,
                  'mb-1': true,
                  'font-weight-normal': true,
                }"
              >
                Firmenkurzbeschreibung
              </div>
              <p class="mb-1 font-weight-normal">Ändern Sie Ihre Kontaktdaten im <a href="account/settings">Profil</a>.</p>
            </div>
            <div
              v-bind:class="{
                'mb-0': true,
                'list-group-item': true,
                'list-group-item-action': true,
                'list-group-item-success': bookingStatus === 'CONFIRMED',
                'list-group-item-danger':
                  bookingStatus === null || bookingStatus === 'CANCELED' || bookingStatus === 'BLOCKED' || bookingStatus === 'PREBOOKED',
              }"
            >
              <div class="d-flex w-100 justify-content-between">
                <h5 class="mb-1">Buchungsstatus</h5>
                <small class="text-body-secondary" v-text="bookingStatus === 'CONFIRMED' ? 'erledigt' : 'offen'"></small>
              </div>
              <p class="mb-1 font-weight-normal" v-if="bookingStatus === 'PREBOOKED'">Bestätigen Sie Ihre Buchung per E-Mail-Link.</p>
              <p class="mb-1 font-weight-normal" v-if="bookingStatus === 'CANCELED'">Ihre Buchung ist storniert.</p>
              <p class="mb-1 font-weight-normal" v-if="bookingStatus === null">
                Nehmen Sie Ihre Buchung <a href="bookabooth">hier</a> vor.
              </p>
            </div>
          </div>
          <p class="mt-4">
            <small class="mr-2 px-2" style="background-color: #f5c6cb">&nbsp;</small> Rot: ausstehende Pflichtangabe<br />
            <small class="mr-2 px-2" style="background-color: #ffeeba">&nbsp;</small> Gelb: ausstehend, aber optional<br />
            <small class="mr-2 px-2" style="background-color: #c3e6cb">&nbsp;</small> Grün: erledigt <br />
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
