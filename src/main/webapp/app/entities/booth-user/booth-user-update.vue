<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="bookaboothApp.boothUser.home.createOrEditLabel" data-cy="BoothUserCreateUpdateHeading">
          Booth User erstellen oder bearbeiten
        </h2>
        <div>
          <div class="form-group" v-if="boothUser.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="boothUser.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booth-user-phone">Phone</label>
            <input
              type="text"
              class="form-control"
              name="phone"
              id="booth-user-phone"
              data-cy="phone"
              :class="{ valid: !v$.phone.$invalid, invalid: v$.phone.$invalid }"
              v-model="v$.phone.$model"
            />
            <div v-if="v$.phone.$anyDirty && v$.phone.$invalid">
              <small class="form-text text-danger" v-for="error of v$.phone.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booth-user-note">Note</label>
            <textarea
              class="form-control"
              name="note"
              id="booth-user-note"
              data-cy="note"
              :class="{ valid: !v$.note.$invalid, invalid: v$.note.$invalid }"
              v-model="v$.note.$model"
            ></textarea>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booth-user-verificationCode">Verification Code</label>
            <input
              type="text"
              class="form-control"
              name="verificationCode"
              id="booth-user-verificationCode"
              data-cy="verificationCode"
              :class="{ valid: !v$.verificationCode.$invalid, invalid: v$.verificationCode.$invalid }"
              v-model="v$.verificationCode.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booth-user-verified">Verified</label>
            <div class="d-flex">
              <input
                id="booth-user-verified"
                data-cy="verified"
                type="datetime-local"
                class="form-control"
                name="verified"
                :class="{ valid: !v$.verified.$invalid, invalid: v$.verified.$invalid }"
                :value="convertDateTimeFromServer(v$.verified.$model)"
                @change="updateZonedDateTimeField('verified', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booth-user-lastLogin">Last Login</label>
            <div class="d-flex">
              <input
                id="booth-user-lastLogin"
                data-cy="lastLogin"
                type="datetime-local"
                class="form-control"
                name="lastLogin"
                :class="{ valid: !v$.lastLogin.$invalid, invalid: v$.lastLogin.$invalid }"
                :value="convertDateTimeFromServer(v$.lastLogin.$model)"
                @change="updateZonedDateTimeField('lastLogin', $event)"
              />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booth-user-disabled">Disabled</label>
            <input
              type="checkbox"
              class="form-check"
              name="disabled"
              id="booth-user-disabled"
              data-cy="disabled"
              :class="{ valid: !v$.disabled.$invalid, invalid: v$.disabled.$invalid }"
              v-model="v$.disabled.$model"
            />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booth-user-user">User</label>
            <select class="form-control" id="booth-user-user" data-cy="user" name="user" v-model="boothUser.user">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="boothUser.user && userOption.id === boothUser.user.id ? boothUser.user : userOption"
                v-for="userOption in users"
                :key="userOption.id"
              >
                {{ userOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="booth-user-company">Company</label>
            <select class="form-control" id="booth-user-company" data-cy="company" name="company" v-model="boothUser.company">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="boothUser.company && companyOption.id === boothUser.company.id ? boothUser.company : companyOption"
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
<script lang="ts" src="./booth-user-update.component.ts"></script>
