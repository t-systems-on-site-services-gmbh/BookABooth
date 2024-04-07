<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="company">
        <h2 class="jh-entity-heading" data-cy="companyDetailsHeading"><span>Company</span> {{ company.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Name</span>
          </dt>
          <dd>
            <span>{{ company.name }}</span>
          </dd>
          <dt>
            <span>Mail</span>
          </dt>
          <dd>
            <span>{{ company.mail }}</span>
          </dd>
          <dt>
            <span>Billing Address</span>
          </dt>
          <dd>
            <span>{{ company.billingAddress }}</span>
          </dd>
          <dt>
            <span>Logo</span>
          </dt>
          <dd>
            <span>{{ company.logo }}</span>
          </dd>
          <dt>
            <span>Description</span>
          </dt>
          <dd>
            <span>{{ company.description }}</span>
          </dd>
          <dt>
            <span>Waiting List</span>
          </dt>
          <dd>
            <span>{{ company.waitingList }}</span>
          </dd>
          <dt>
            <span>Exhibitor List</span>
          </dt>
          <dd>
            <span>{{ company.exhibitorList }}</span>
          </dd>
          <dt>
            <span>Department</span>
          </dt>
          <dd>
            <span v-for="(department, i) in company.departments" :key="department.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'DepartmentView', params: { departmentId: department.id } }">{{ department.id }}</router-link>
            </span>
          </dd>
          <dt>
            <span>Contact</span>
          </dt>
          <dd>
            <span v-for="(contact, i) in company.contacts" :key="contact.id"
              >{{ i > 0 ? ', ' : '' }}
              <router-link :to="{ name: 'ContactView', params: { contactId: contact.id } }">{{ contact.id }}</router-link>
            </span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span>Zurück</span>
        </button>
        <router-link v-if="company.id" :to="{ name: 'CompanyEdit', params: { companyId: company.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span>Bearbeiten</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./company-details.component.ts"></script>
