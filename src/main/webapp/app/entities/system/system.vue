<template>
  <div>
    <h2 id="page-heading" data-cy="SystemHeading">
      <span id="system-heading">Anmeldung freischalten/sperren</span>
    </h2>
    <br />
    <div class="alert alert-error" v-if="!isFetching && !system">
      <span>Kein System gefunden</span>
    </div>
    <div v-if="!isFetching && system">
      <p>
        Die Anmeldung ist
        <b>
          <span v-if="system.enabled">freigeschaltet</span>
          <span v-if="!system.enabled">gesperrt</span>.
        </b>
      </p>
    </div>
    <div v-if="!isFetching && system">
      <b-button class="btn" variant="primary" @click="toggleSysEnabled()">
        <span v-if="system.enabled">Sperren</span>
        <span v-if="!system.enabled">Freischalten</span>
      </b-button>
    </div>
  </div>
</template>

<script lang="ts" src="./system.component.ts"></script>
