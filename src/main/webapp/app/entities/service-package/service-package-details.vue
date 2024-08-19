<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="servicePackage">
        <h2 class="jh-entity-heading" data-cy="servicePackageDetailsHeading"><span>Service Paket</span> {{ servicePackage.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ servicePackage.name }}</span>
          </dd>
          <dt>
            <span>Preis</span>
          </dt>
          <dd>
            <span>{{ servicePackage.price }}</span>
          </dd>
          <dt>
            <span>Beschreibung</span>
          </dt>
          <dd>
            <span>{{ servicePackage.description }}</span>
          </dd>
          <dt>
            <span>Stand</span>
          </dt>
          <dd>
            <span v-for="(booth, i) in servicePackage.booths" :key="booth.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'BoothView', params: { boothId: booth.id } }">{{ booth.id }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Zurück</span>
        </button>
        <router-link
          v-if="servicePackage.id"
          :to="{ name: 'ServicePackageEdit', params: { servicePackageId: servicePackage.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Bearbeiten</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./service-package-details.component.ts"></script>
