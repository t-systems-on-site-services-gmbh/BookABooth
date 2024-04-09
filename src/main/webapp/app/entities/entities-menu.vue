<template>
  <div>
    <b-dropdown-item to="/system">
      <font-awesome-icon icon="asterisk" />
      <span>Anmeldung freischalten/sperren</span>
    </b-dropdown-item>
    <b-dropdown-item to="/booth">
      <font-awesome-icon icon="asterisk" />
      <span>Lageplan</span>
    </b-dropdown-item>
    <b-dropdown-item to="/service-package">
      <font-awesome-icon icon="asterisk" />
      <span>Service Pakete</span>
    </b-dropdown-item>
    <b-dropdown-item to="/booking">
      <font-awesome-icon icon="asterisk" />
      <span>Buchungen</span>
    </b-dropdown-item>
    <b-dropdown-item to="/booth-user">
      <font-awesome-icon icon="asterisk" />
      <span>Benutzerverwaltung</span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>
