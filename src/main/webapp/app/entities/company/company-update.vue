<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="bookaboothApp.company.home.createOrEditLabel" data-cy="CompanyCreateUpdateHeading">Company erstellen oder bearbeiten</h2>
        <div>
          <div class="form-group" v-if="company.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="company.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="company-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-mail">Mail</label>
            <input
              type="text"
              class="form-control"
              name="mail"
              id="company-mail"
              data-cy="mail"
              :class="{ valid: !v$.mail.$invalid, invalid: v$.mail.$invalid }"
              v-model="v$.mail.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-billingAddress">Billing Address</label>
            <input
              type="text"
              class="form-control"
              name="billingAddress"
              id="company-billingAddress"
              data-cy="billingAddress"
              :class="{ valid: !v$.billingAddress.$invalid, invalid: v$.billingAddress.$invalid }"
              v-model="v$.billingAddress.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-logo">Logo</label>
            <input
              type="text"
              class="form-control"
              name="logo"
              id="company-logo"
              data-cy="logo"
              :class="{ valid: !v$.logo.$invalid, invalid: v$.logo.$invalid }"
              v-model="v$.logo.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-description">Description</label>
            <input
              type="text"
              class="form-control"
              name="description"
              id="company-description"
              data-cy="description"
              :class="{ valid: !v$.description.$invalid, invalid: v$.description.$invalid }"
              v-model="v$.description.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-waitingList">Waiting List</label>
            <input
              type="checkbox"
              class="form-check"
              name="waitingList"
              id="company-waitingList"
              data-cy="waitingList"
              :class="{ valid: !v$.waitingList.$invalid, invalid: v$.waitingList.$invalid }"
              v-model="v$.waitingList.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="company-exhibitorList">Exhibitor List</label>
            <input
              type="checkbox"
              class="form-check"
              name="exhibitorList"
              id="company-exhibitorList"
              data-cy="exhibitorList"
              :class="{ valid: !v$.exhibitorList.$invalid, invalid: v$.exhibitorList.$invalid }"
              v-model="v$.exhibitorList.$model"
            />
          </div>
          <div class="form-group">
            <label for="company-department">Department</label>
            <select
              class="form-control"
              id="company-departments"
              data-cy="department"
              multiple
              name="department"
              v-if="company.departments !== undefined"
              v-model="company.departments"
            >
              <option
                v-bind:value="getSelected(company.departments, departmentOption, 'id')"
                v-for="departmentOption in departments"
                :key="departmentOption.id"
              >
                {{ departmentOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label for="company-contact">Contact</label>
            <select
              class="form-control"
              id="company-contacts"
              data-cy="contact"
              multiple
              name="contact"
              v-if="company.contacts !== undefined"
              v-model="company.contacts"
            >
              <option
                v-bind:value="getSelected(company.contacts, contactOption, 'id')"
                v-for="contactOption in contacts"
                :key="contactOption.id"
              >
                {{ contactOption.id }}
              </option>
            </select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>Abbrechen</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Speichern</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./company-update.component.ts"></script>
