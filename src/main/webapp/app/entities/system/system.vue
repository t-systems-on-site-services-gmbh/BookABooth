<template>
  <div>
    <p class="h5 ml-2">Systemfreischaltung:</p>
    <div class="row ml-2">
      <div class="alert alert-error" v-if="!isFetching && !system">
        <span>Kein System gefunden</span>
      </div>
      <div v-if="!isFetching && system" class="border border-danger rounded p-3 pl-4 pr-4">
        <div class="custom-control custom-switch" style="transform: scale(1.1)">
          <input type="checkbox" class="custom-control-input" id="customSwitch1" @click="toggleSysEnabled()" :checked="system.enabled" />
          <label class="custom-control-label" for="customSwitch1" v-if="system.enabled">Die Anmeldung ist <b>freigeschaltet.</b></label>
          <label class="custom-control-label" for="customSwitch1" v-if="!system.enabled">Die Anmeldung ist <b>gesperrt.</b></label>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./system.component.ts"></script>
