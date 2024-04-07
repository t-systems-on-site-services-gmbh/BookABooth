<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="boothUser">
        <h2 class="jh-entity-heading" data-cy="boothUserDetailsHeading"><span>Booth User</span> {{ boothUser.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Phone</span>
          </dt>
          <dd>
            <span>{{ boothUser.phone }}</span>
          </dd>
          <dt>
            <span>Note</span>
          </dt>
          <dd>
            <span>{{ boothUser.note }}</span>
          </dd>
          <dt>
            <span>Verification Code</span>
          </dt>
          <dd>
            <span>{{ boothUser.verificationCode }}</span>
          </dd>
          <dt>
            <span>Verified</span>
          </dt>
          <dd>
            <span v-if="boothUser.verified">{{ formatDateLong(boothUser.verified) }}</span>
          </dd>
          <dt>
            <span>Last Login</span>
          </dt>
          <dd>
            <span v-if="boothUser.lastLogin">{{ formatDateLong(boothUser.lastLogin) }}</span>
          </dd>
          <dt>
            <span>Disabled</span>
          </dt>
          <dd>
            <span>{{ boothUser.disabled }}</span>
          </dd>
          <dt>
            <span>User</span>
          </dt>
          <dd>
            {{ boothUser.user ? boothUser.user.id : '' }}
          </dd>
          <dt>
            <span>Company</span>
          </dt>
          <dd>
            <div v-if="boothUser.company">
              <router-link :to="{ name: 'CompanyView', params: { companyId: boothUser.company.id } }">{{
                boothUser.company.id
              }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Zurück</span>
        </button>
        <router-link
          v-if="boothUser.id"
          :to="{ name: 'BoothUserEdit', params: { boothUserId: boothUser.id } }"
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

<script lang="ts" src="./booth-user-details.component.ts"></script>
