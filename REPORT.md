# Submission Report

Track: Master / MLSD
Student: AKIL Wael

## Overview

This submission presents a distributed todo platform implemented with two Spring Boot REST services, containerized with Docker, and deployed on Kubernetes. The objective was to implement a complete microservices workflow from development to deployment, including communication between services, data persistence, and production-style security controls.

The architecture is intentionally split by responsibility. `MyService` acts as the public API gateway for client interactions and handles orchestration logic. `MyService2` is an internal domain service responsible for todo operations and persistence access. This separation of concerns improves maintainability, limits coupling between components, and reflects a standard distributed system pattern where one service aggregates and delegates to another.

The implementation was validated on Minikube and structured with Kubernetes manifests to keep deployment reproducible. The final result is a functional application with clear service boundaries, inter-service calls, operational resilience, and secure cluster configuration.

## Service Communication

External access is provided through NGINX ingress on `myservice.info`. Incoming HTTP traffic is routed to `MyService`, which communicates with `MyService2` using internal Kubernetes DNS-based service discovery. This setup applies north-south and east-west communication patterns in Kubernetes.

The API surface includes `GET /`, `GET /aggregate`, `GET /todos`, `POST /todos`, `GET /todos/summary`, and `GET /dashboard`. The `aggregate` endpoint confirms runtime service-to-service communication. The `dashboard` endpoint combines multiple pieces of information into a single response for the client.

Client-facing behavior is composed from internal service interactions rather than a single monolithic component. This approach enables clearer responsibilities, independent scaling potential, and easier evolution of each service over time.

## Data Persistence

Persistence is provided by PostgreSQL, deployed as a dedicated workload in the same namespace. Storage is backed by a PersistentVolumeClaim to ensure data remains available across pod restarts and rollout events. `MyService2` uses this data layer for todo creation, retrieval, and summary calculations.

Validation included end-to-end functional calls on `/`, `/aggregate`, `/todos`, and `/dashboard`, followed by a controlled restart of `myservice2` to confirm that existing todo data remained intact. This behavior confirms that state is decoupled from stateless service pods, which is a core requirement for reliable distributed systems.

This design also improves operational stability: services can be restarted, rescheduled, or upgraded without losing business data, as long as the storage claim remains attached and healthy.

## Security

Security controls were integrated as part of the core design rather than added at the end. Each workload uses a dedicated service account, and RBAC is scoped to a read-only audit identity to enforce least privilege. Database credentials are handled through Kubernetes secrets rather than hardcoded values.

Network isolation is enforced through a default-deny policy model combined with explicit allow rules for required paths. This reduces lateral movement risk and makes permitted communication paths explicit and auditable.

Container hardening was applied to the application workloads through non-root execution, dropped Linux capabilities, disabled privilege escalation, read-only root filesystems, and runtime-default seccomp configuration. These controls reduce attack surface and align with common secure-by-default Kubernetes practices.

## Service Mesh

Istio is integrated with strict namespace-level mTLS, ensuring encrypted and authenticated service-to-service communication inside the cluster. In addition, authorization policies were applied to enforce only intended traffic paths: `ingress -> myservice -> myservice2 -> postgres`.

This mesh layer provides an additional enforcement boundary on top of Kubernetes network policies. Together, these controls strengthen zero-trust behavior within the namespace by validating both who is communicating and which paths are allowed.

The resulting setup uses service mesh features for secure east-west traffic control and policy-based communication governance.

## Published Images

- `drtxu/myservice:submission-20260403`
- `drtxu/myservice2:submission-20260403`

## Evidence

All submission evidence is provided as PNG files in `/evidence`.

- `architecture.png`: architecture diagram showing the flow `Ingress -> MyService -> MyService2 -> Postgres`.
- `pods.png`: running state of application pods in namespace `todo-platform`.
- `services-ingress-networkpolicy.png`: services, ingress exposure, and applied network policies.
- `security.png`: security-related cluster objects and policy posture.
- `aggregate.png`: successful `/aggregate` call proving service-to-service communication.
- `todos.png`: todo list response showing stored todo data.
- `dashboard.png`: combined dashboard response with greeting, summary, and todos.
- `persistence-after-restart.png`: data persistence validation after restarting `myservice2`.

## Conclusion

The final platform is functional, reproducible, and aligned with production-oriented engineering practices for microservice-based systems.
