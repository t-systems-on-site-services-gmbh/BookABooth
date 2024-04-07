<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="contact">
        <h2 class="jh-entity-heading" data-cy="contactDetailsHeading"><span>Contact</span> {{ contact.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>First Name</span>
          </dt>
          <dd>
            <span>{{ contact.firstName }}</span>
          </dd>
          <dt>
            <span>Last Name</span>
          </dt>
          <dd>
            <span>{{ contact.lastName }}</span>
          </dd>
          <dt>
            <span>Mail</span>
          </dt>
          <dd>
            <span>{{ contact.mail }}</span>
          </dd>
          <dt>
            <span>Phone</span>
          </dt>
          <dd>
            <span>{{ contact.phone }}</span>
          </dd>
          <dt>
            <span>Responsibility</span>
          </dt>
          <dd>
            <span>{{ contact.responsibility }}</span>
          </dd>
          <dt>
            <span>Note</span>
          </dt>
          <dd>
            <span>{{ contact.note }}</span>
          </dd>
          <dt>
            <span>Company</span>
          </dt>
          <dd>
            <span v-for="(company, i) in contact.companies" :key="company.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'CompanyView', params: { companyId: company.id } }">{{ company.id }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Zurück</span>
        </button>
        <router-link v-if="contact.id" :to="{ name: 'ContactEdit', params: { contactId: contact.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Bearbeiten</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./contact-details.component.ts"></script>
