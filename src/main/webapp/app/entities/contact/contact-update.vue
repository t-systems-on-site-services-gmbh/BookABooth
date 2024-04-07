<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="bookaboothApp.contact.home.createOrEditLabel" data-cy="ContactCreateUpdateHeading">Contact erstellen oder bearbeiten</h2>
        <div>
          <div class="form-group" v-if="contact.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="contact.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="contact-firstName">First Name</label>
            <input
              type="text"
              class="form-control"
              name="firstName"
              id="contact-firstName"
              data-cy="firstName"
              :class="{ valid: !v$.firstName.$invalid, invalid: v$.firstName.$invalid }"
              v-model="v$.firstName.$model"
            />
            <div v-if="v$.firstName.$anyDirty && v$.firstName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.firstName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="contact-lastName">Last Name</label>
            <input
              type="text"
              class="form-control"
              name="lastName"
              id="contact-lastName"
              data-cy="lastName"
              :class="{ valid: !v$.lastName.$invalid, invalid: v$.lastName.$invalid }"
              v-model="v$.lastName.$model"
            />
            <div v-if="v$.lastName.$anyDirty && v$.lastName.$invalid">
              <small class="form-text text-danger" v-for="error of v$.lastName.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="contact-mail">Mail</label>
            <input
              type="text"
              class="form-control"
              name="mail"
              id="contact-mail"
              data-cy="mail"
              :class="{ valid: !v$.mail.$invalid, invalid: v$.mail.$invalid }"
              v-model="v$.mail.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="contact-phone">Phone</label>
            <input
              type="text"
              class="form-control"
              name="phone"
              id="contact-phone"
              data-cy="phone"
              :class="{ valid: !v$.phone.$invalid, invalid: v$.phone.$invalid }"
              v-model="v$.phone.$model"
            />
            <div v-if="v$.phone.$anyDirty && v$.phone.$invalid">
              <small class="form-text text-danger" v-for="error of v$.phone.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="contact-responsibility">Responsibility</label>
            <select
              class="form-control"
              name="responsibility"
              :class="{ valid: !v$.responsibility.$invalid, invalid: v$.responsibility.$invalid }"
              v-model="v$.responsibility.$model"
              id="contact-responsibility"
              data-cy="responsibility"
            >
              <option
                v-for="contactResponsibility in contactResponsibilityValues"
                :key="contactResponsibility"
                v-bind:value="contactResponsibility"
              >
                {{ contactResponsibility }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="contact-note">Note</label>
            <textarea
              class="form-control"
              name="note"
              id="contact-note"
              data-cy="note"
              :class="{ valid: !v$.note.$invalid, invalid: v$.note.$invalid }"
              v-model="v$.note.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label for="contact-company">Company</label>
            <select
              class="form-control"
              id="contact-companies"
              data-cy="company"
              multiple
              name="company"
              v-if="contact.companies !== undefined"
              v-model="contact.companies"
            >
              <option
                v-bind:value="getSelected(contact.companies, companyOption, 'id')"
                v-for="companyOption in companies"
                :key="companyOption.id"
              >
                {{ companyOption.id }}
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
<script lang="ts" src="./contact-update.component.ts"></script>
