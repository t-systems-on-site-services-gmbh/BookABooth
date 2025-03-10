<template>
  <div v-if="checklist">
    <h4 class="mb-2 mt-4">Checkliste und Status:</h4>
    <div v-if="checklist.verified === false" class="d-flex justify-content-center my-2">
      <div class="spinner-border" role="status">
        <span class="sr-only">Loading...</span>
      </div>
    </div>
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
        'list-group-item-success': checklist.verified,
        'list-group-item-danger': !checklist.verified,
      }"
    >
      <div class="d-flex w-100 justify-content-between">
        <h5 class="mb-1">Verifizierung</h5>
        <small class="text-body-secondary" v-text="checklist.verified ? 'erledigt' : 'offen'"></small>
      </div>
      <p class="mb-1 font-weight-normal">Account ist verifiziert.</p>
    </div>
    <div
      v-bind:class="{
        'mb-0': true,
        'list-group-item': true,
        'list-group-item-action': true,
        'list-group-item-success': checklist.address && checklist.logo && checklist.phoneNumber && checklist.companyDescription,
        'list-group-item-danger': !(checklist.address && checklist.logo && checklist.phoneNumber && checklist.companyDescription),
      }"
    >
      <div class="d-flex w-100 justify-content-between">
        <h5 class="mb-1">Kontaktdaten</h5>
        <small
          class="text-body-secondary"
          v-text="checklist.address && checklist.logo && checklist.phoneNumber && checklist.companyDescription ? 'erledigt' : 'offen'"
        ></small>
      </div>
      <div
        :class="{
          'list-group-item': true,
          'list-group-item-danger': !checklist.address,
          'list-group-item-success': checklist.address,
          'mb-0': true,
          'font-weight-normal': true,
          'd-flex': true,
          'justify-content-between': true,
        }"
      >
        <span class="text">Rechnungsanschrift</span
        ><router-link :to="{ path: '/account/settings', hash: '#headingOne' }" v-if="!checklist.address">
          <font-awesome-icon icon="arrow-right" />
        </router-link>
      </div>
      <div
        :class="{
          'list-group-item': true,
          'list-group-item-danger': !checklist.logo,
          'list-group-item-success': checklist.logo,
          'mb-0': true,
          'font-weight-normal': true,
          'd-flex': true,
          'justify-content-between': true,
        }"
      >
        <span class="text">Logo des Unternehmens</span
        ><router-link :to="{ path: '/account/settings', hash: '#headingOne' }" v-if="!checklist.logo">
          <font-awesome-icon icon="arrow-right" />
        </router-link>
      </div>
      <div
        :class="{
          'list-group-item': true,
          'list-group-item-danger': !checklist.phoneNumber,
          'list-group-item-success': checklist.phoneNumber,
          'mb-0': true,
          'font-weight-normal': true,
          'd-flex': true,
          'justify-content-between': true,
        }"
      >
        <span class="text">Telefonnummer</span
        ><router-link :to="{ path: '/account/settings', hash: '#headingTwo' }" v-if="!checklist.phoneNumber">
          <font-awesome-icon icon="arrow-right" />
        </router-link>
      </div>
      <div
        :class="{
          'list-group-item': true,
          'list-group-item-danger': !checklist.companyDescription,
          'list-group-item-success': checklist.companyDescription,
          'mb-1': true,
          'font-weight-normal': true,
          'd-flex': true,
          'justify-content-between': true,
        }"
      >
        <span class="text">Firmenkurzbeschreibung</span
        ><router-link :to="{ path: '/account/settings', hash: '#headingOne' }" v-if="!checklist.companyDescription">
          <font-awesome-icon icon="arrow-right" />
        </router-link>
      </div>
      <p class="mb-1 font-weight-normal">Ändern Sie Ihre Kontaktdaten im <a href="account/settings">Profil</a>.</p>
    </div>
    <div
      v-bind:class="{
        'mb-0': true,
        'list-group-item': true,
        'list-group-item-action': true,
        'list-group-item-success': checklist.bookingStatus === 'CONFIRMED',
        'list-group-item-danger':
          checklist.bookingStatus === null || checklist.bookingStatus === 'CANCELED' || checklist.bookingStatus === 'BLOCKED',
      }"
    >
      <div class="d-flex w-100 justify-content-between">
        <h5 class="mb-1">Buchung</h5>
        <small class="text-body-secondary" v-text="checklist.bookingStatus === 'CONFIRMED' ? 'erledigt' : 'offen'"></small>
      </div>
      <p v-if="checklist.bookingStatus === 'CONFIRMED'" class="mb-1 font-weight-normal">
        Sie haben einen Stand gebucht. Details finden Sie <a href="bookabooth">hier</a>. <br />
        Sie können Ihre Buchung über Ihr <a href="/account/settings">Profil</a> stornieren.
      </p>
      <p v-else-if="!system.enabled">
        Die Standbuchung ist systemseitig nicht freigegeben.
        <span v-if="!checklist.address || !checklist.logo || !checklist.phoneNumber || !checklist.companyDescription">
          Nutzen Sie die Zeit, um Ihre Kontaktdaten zu komplettieren.
        </span>
      </p>
      <p v-else-if="!checklist.address || !checklist.logo || !checklist.phoneNumber || !checklist.companyDescription">
        Bitte vervollständigen Sie Ihre Kontaktdaten, um einen Stand buchen zu können.
      </p>
      <p v-else-if="checklist.bookingStatus === 'CANCELED'" class="mb-1 font-weight-normal">
        Ihre Buchung wurde storniert. Sie können <a href="bookabooth">hier</a> eine erneute Buchung vornehmen.
      </p>
      <p v-else-if="checklist.bookingStatus === 'BLOCKED'" class="mb-1 font-weight-normal">
        Es wird gerade eine Buchung für Ihre Firma vorgenommen. Bitte warten Sie, bis die Buchung abgeschlossen ist. Sollten Sie eine
        Standbuchung abgebrochen haben, so wird der ausgewählte Stand nach einiger Zeit automatisch freigegeben.
      </p>
      <p v-else-if="checklist.bookingStatus === null && !allBoothsOccupied" class="mb-1 font-weight-normal">
        Nehmen Sie Ihre Buchung <a href="bookabooth">hier</a> vor.
      </p>
      <p v-else-if="checklist.bookingStatus === null && allBoothsOccupied && !account.company.waitingList" class="mb-1 font-weight-normal">
        Alle Stände sind bereits gebucht. Sie können sich <span @click="addToWaitingList" class="link">hier</span> für die Warteliste
        eintragen.
      </p>
      <p v-else-if="checklist.bookingStatus === null && allBoothsOccupied && account.company.waitingList" class="mb-1 font-weight-normal">
        Sie befinden sich auf der Warteliste. Sie erhalten eine E-Mail, wenn Stände wieder verfügbar sind.
      </p>
    </div>
    <p class="mt-4">
      <small class="mr-2 px-2" style="background-color: #f5c6cb">&nbsp;</small> Rot: ausstehende Pflichtangabe<br />
      <small class="mr-2 px-2" style="background-color: #c3e6cb">&nbsp;</small> Grün: erledigt <br />
    </p>
  </div>
</template>

<style lang="scss" scoped>
@import '/content/scss/_bootstrap-variables.scss';
.link {
  color: $jhs_color_red;
  cursor: pointer;
  text-decoration: none;
  font-weight: bold;
}
.link:hover {
  color: darken($jhs_color_red, 10%);
  cursor: pointer;
  text-decoration: underline;
}
.width-on-pc {
  @media (min-width: 768px) {
    width: 50%;
  }
}
</style>

<script lang="ts" src="./user-checklist.component.ts"></script>
