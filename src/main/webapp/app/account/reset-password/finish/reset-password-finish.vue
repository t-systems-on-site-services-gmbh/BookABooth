<template>
  <div>
    <div class="row justify-content-center">
      <div class="col-md-8">
        <h1>Passwort zurücksetzen</h1>

        <div class="alert alert-danger" v-if="keyMissing">Der Schlüssel zum Zurücksetzen fehlt.</div>

        <div class="alert alert-danger" v-if="error">
          <p>Ihr Passwort konnte nicht zurückgesetzt werden. Die Zeit wurde überschritten.</p>
        </div>

        <div class="alert alert-success" v-if="success">
          <span><strong>Ihr Passwort wurde zurückgesetzt.</strong> Bitte </span>
          <a class="alert-link" v-on:click="openLogin">anmelden</a>
        </div>
        <div class="alert alert-danger" v-if="doNotMatch">
          <p>Das bestätigte Passwort entspricht nicht dem neuen Passwort!</p>
        </div>

        <div class="alert alert-warning" v-if="!success && !keyMissing">
          <p>Wählen Sie ein neues Passwort</p>
        </div>

        <div v-if="!keyMissing">
          <form v-if="!success" name="form" role="form" v-on:submit.prevent="finishReset()">
            <div class="form-group">
              <label class="form-control-label" for="newPassword">Neues Passwort</label>
              <input
                type="password"
                class="form-control"
                id="newPassword"
                name="newPassword"
                placeholder="Neues Passwort"
                :class="{ valid: !v$.resetAccount.newPassword.$invalid, invalid: v$.resetAccount.newPassword.$invalid }"
                v-model="v$.resetAccount.newPassword.$model"
                minlength="12"
                maxlength="50"
                required
                data-cy="resetPassword"
              />
              <div v-if="v$.resetAccount.newPassword.$anyDirty && v$.resetAccount.newPassword.$invalid">
                <small class="form-text text-danger" v-if="v$.resetAccount.newPassword.required.$invalid"
                  >Ein neues Passwort wird benötigt.</small
                >
                <small class="form-text text-danger" v-if="v$.resetAccount.newPassword.minLength.$invalid"
                  >Das neue Passwort muss mindestens 12 Zeichen lang sein</small
                >
                <small class="form-text text-danger" v-if="v$.resetAccount.newPassword.maxLength.$invalid"
                  >Das neue Passwort darf nicht länger als 50 Zeichen sein</small
                >
                <small class="form-text text-danger" v-if="v$.resetAccount.newPassword.pattern.$invalid">
                  Das Passwort muss mindestens einen Großbuchstaben, einen Kleinbuchstaben, eine Zahl und ein Sonderzeichen
                  (@$!%*?+_=)(#><.:&"'|~^/\\\]\[{}) enthalten.
                </small>
              </div>
            </div>
            <div class="form-group">
              <label class="form-control-label" for="confirmPassword">Neues Passwort bestätigen</label>
              <input
                type="password"
                class="form-control"
                id="confirmPassword"
                name="confirmPassword"
                :class="{ valid: !v$.resetAccount.confirmPassword.$invalid, invalid: v$.resetAccount.confirmPassword.$invalid }"
                placeholder="Bestätigen Sie Ihr neues Passwort"
                v-model="v$.resetAccount.confirmPassword.$model"
                minlength="4"
                maxlength="50"
                required
                data-cy="confirmResetPassword"
              />
              <div v-if="v$.resetAccount.confirmPassword.$anyDirty && v$.resetAccount.confirmPassword.$invalid">
                <small class="form-text text-danger" v-if="!v$.resetAccount.confirmPassword.sameAsPassword"
                  >Das bestätigte Passwort entspricht nicht dem neuen Passwort!</small
                >
              </div>
            </div>
            <button type="submit" :disabled="v$.resetAccount.$invalid" class="btn btn-primary" data-cy="submit">Speichern</button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./reset-password-finish.component.ts"></script>
