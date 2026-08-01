# Manual setup checklist

These actions require accounts, infrastructure, or credentials that cannot safely be created in source code.

## 1. Solo Git workflow

1. Open `https://learn.reboot01.com/git/malasfoor/travel-plan` and create/confirm the repository.
2. In this directory run `git remote -v`.
3. If `origin` is missing or wrong, run:
   `git remote set-url origin https://learn.reboot01.com/git/malasfoor/travel-plan.git`
4. Since this is a solo project, you can commit and push directly if your evaluator does not require PR evidence. A safer solo workflow is still to create a feature branch and merge it after the CI build passes.

The published project rubric explicitly mentions Pull Requests and approvals. If your instructor has waived that requirement for solo work, follow their instruction; otherwise, a self-authored PR can still provide CI and review history even without another contributor.

## 2. Create local secrets

1. Run `Copy-Item .env.example .env`.
2. Generate a JWT secret of at least 32 random bytes (prefer 64 characters).
3. Replace every `change-me` value and choose a 12+ character admin password containing uppercase, lowercase, number, and symbol.
4. Never commit `.env`; it is already ignored.

If PostgreSQL was previously started with older credentials, changing `.env` does not rewrite the existing volume. For disposable local data only, stop the stack and deliberately run `docker compose down --volumes`, then start it again.

## 3. Jenkins and SonarQube

1. Install Jenkins on a Linux CI host with Java 21, Maven 3.9+, Docker Engine, and Docker Compose.
2. Install Jenkins plugins: Pipeline, Git, JUnit, SonarQube Scanner, and Docker Pipeline.
3. In **Manage Jenkins > Tools**, name the JDK `jdk-21` and Maven `maven-3.9` (the names expected by `Jenkinsfile`).
4. Create a SonarQube project and token.
5. In **Manage Jenkins > Credentials**, add the Sonar token as a secret text credential.
6. In **Manage Jenkins > System > SonarQube servers**, add the server with the name `sonarqube` and that credential.
7. In SonarQube, configure the Jenkins webhook: `https://YOUR-JENKINS/sonarqube-webhook/`.
8. Create a Multibranch Pipeline pointing at this repository and scan branches.

## 4. Pull Request protection

In the Git server settings for `main`:

1. Prevent direct pushes and force-pushes.
2. Require at least one approval.
3. Require the Jenkins build and SonarQube quality gate.
4. Require all review comments to be resolved.
5. Require branches to be current before merge.

## 5. Production TLS and DNS

1. Provision a Linux host and point a public DNS A/AAAA record to it.
2. Allow inbound TCP 80 and 443 only; do not expose service or database ports.
3. Install Docker/Compose and run `ansible-galaxy collection install -r ansible/requirements.yml`.
4. Copy `ansible/inventory/hosts.example.yml`, replace the host and SSH user, and run:
   `ansible-playbook -i ansible/inventory/hosts.yml ansible/playbooks/deploy.yml`
5. Put a TLS reverse proxy (Caddy, Traefik, or Nginx + Certbot) in front of gateway port 8080. Configure automatic Let's Encrypt renewal and redirect HTTP to HTTPS.
6. Confirm externally that only 80/443 are reachable and test renewal with the proxy's staging/dry-run procedure.

## 6. Vault and payment credentials

1. Deploy HashiCorp Vault outside this Compose stack (production Vault must be initialized, unsealed, backed up, and protected separately).
2. Enable a KV v2 mount and store Stripe and PayPal secret keys there.
3. Create a read-only policy limited to this application's paths and authenticate workloads with AppRole/Kubernetes auth, not a root token.
4. Inject secrets into services at deployment time. Never put provider secret keys in payment-method records, `.env`, Jenkinsfiles, or Git.
5. Use Stripe and PayPal sandbox credentials until end-to-end payment flows are implemented and reviewed.

## 7. Acceptance checks

1. Run `mvn --batch-mode clean verify` and `docker compose config --quiet`.
2. Start the stack and confirm all containers become healthy.
3. Log in as admin and create, edit, and delete one user, travel, and payment method.
4. Confirm requests without a token return 401 and a non-admin token returns 403.
5. Open Grafana: confirm Prometheus targets are up and query Loki with `{service=~"gateway|user-service|travel-service|payment-service"}`.
6. Stop one replica and confirm requests continue through the remaining replica.
