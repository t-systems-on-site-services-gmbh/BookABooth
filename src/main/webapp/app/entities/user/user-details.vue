<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="user">
        <h2 class="jh-entity-heading" data-cy="userDetailsHeading"><span>Benutzer</span> {{ user.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Login</span>
          </dt>
          <dd>
            <span>{{ user.login }}</span>
          </dd>
          <dt>
            <span>Vorname</span>
          </dt>
          <dd>
            <span>{{ user.firstName }}</span>
          </dd>
          <dt>
            <span>Nachname</span>
          </dt>
          <dd>
            <span>{{ user.lastName }}</span>
          </dd>
          <dt>
            <span>Firma</span>
          </dt>
          <dd>
            <span>{{ user.companyName }}</span>
          </dd>
          <dt>
            <span>E-Mail</span>
          </dt>
          <dd>
            <span>{{ user.email }}</span>
          </dd>
          <dt>
            <span>Role</span>
          </dt>
          <dd>
            <span>{{ user.authorities }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Zurück</span>
        </button>
        <router-link v-if="user.id" :to="{ name: 'UserEdit', params: { userId: user.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Bearbeiten</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./user-details.component.ts"></script>