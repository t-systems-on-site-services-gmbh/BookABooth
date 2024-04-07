<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="system">
        <h2 class="jh-entity-heading" data-cy="systemDetailsHeading"><span>System</span> {{ system.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Enabled</span>
          </dt>
          <dd>
            <span>{{ system.enabled }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Zurück</span>
        </button>
        <router-link v-if="system.id" :to="{ name: 'SystemEdit', params: { systemId: system.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Bearbeiten</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./system-details.component.ts"></script>
