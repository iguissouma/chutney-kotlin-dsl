# Changelog

## [0.1.15](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.15)
[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.14...0.1.15)

**Merged pull requests:**

- feat: filter on method + bugfix: NPE on environment + no unique id on final tasks [\#48](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/48) ([nbrouand](https://github.com/nbrouand))

## [0.1.14](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.14)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.13...0.1.14)

**Merged pull requests:**

- chore/json compare mode [\#45](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/45) ([boddissattva](https://github.com/boddissattva))
- task: Add SFTP ls, attributes, upload and download [\#44](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/44) ([bessonm](https://github.com/bessonm))
- feat/junit engine [\#27](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/27) ([boddissattva](https://github.com/boddissattva))


## [0.1.13](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.13)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.12...0.1.13)

**Merged pull requests:**

- feat: Add SCP upload/download [\#43](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/43) ([bessonm](https://github.com/bessonm))
- Update functions [\#42](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/42) ([boddissattva](https://github.com/boddissattva))
- Kafka publish consume tasks [\#41](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/41) ([boddissattva](https://github.com/boddissattva))


## [0.1.12](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.12)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.11...0.1.12)

# Breaking change : 

KafkaBasicConsumeTask => properties is now defaulted to null (was Map<String, String> = mapOf("auto.offset.reset" to "earliest")) 
SeleniumGetTask() => selector is now string, was boolean

**Implemented enhancements:**

- 🚀| Keep all execution reports in target/ folder [\#36](https://github.com/chutney-testing/chutney-kotlin-dsl/issues/36)

**Fixed bugs:**

- 🐛 | Jms tasks input defaulted \(should not be\) [\#39](https://github.com/chutney-testing/chutney-kotlin-dsl/issues/39)

**Merged pull requests:**

- feat: Keep previous execution reports [\#37](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/37) ([bessonm](https://github.com/bessonm))
- chore: Add Github Templates [\#35](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/35) ([bessonm](https://github.com/bessonm))



## [0.1.11](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.11) (2021-12-01)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.10...0.1.11)

**Merged pull requests:**

- fix/null json inputs [\#34](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/34) ([boddissattva](https://github.com/boddissattva))
- feat: HTTP server keypassword & Missing param on HttpsListenerTask [\#33](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/33) ([bessonm](https://github.>
- chore\(\): Update JUnit5 version to 5.8.1 [\#32](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/32) ([boddissattva](https://github.com/boddissattva))
- fix: Remove default HTTP output [\#31](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/31) ([bessonm](https://github.com/bessonm))
- feat/radius [\#30](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/30) ([boddissattva](https://github.com/boddissattva))
- fix: Reverse actual & expected assertion values [\#29](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/29) ([bessonm](https://github.com/bessonm))
- bugfix: FinalTask DSL is missing 'target' argument [\#26](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/26) ([bessonm](https://github.com/bessonm))

## [0.1.10](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.10) (2021-10-12)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.9...0.1.10)

**Closed issues:**

- Upgrade to jdk 11 [\#21](https://github.com/chutney-testing/chutney-kotlin-dsl/issues/21)
- Upgrade jackson-module-kotlin which have vulnabilirity [\#20](https://github.com/chutney-testing/chutney-kotlin-dsl/issues/20)
- When releasing add github release [\#19](https://github.com/chutney-testing/chutney-kotlin-dsl/issues/19)
- Add non existing DSL Chutney tasks [\#15](https://github.com/chutney-testing/chutney-kotlin-dsl/issues/15)
- Add all changelog since 0.1.0 in changelog [\#14](https://github.com/chutney-testing/chutney-kotlin-dsl/issues/14)
- Add github release push to release github action [\#13](https://github.com/chutney-testing/chutney-kotlin-dsl/issues/13)

**Merged pull requests:**

- Add an example project / Clean and update project [\#25](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/25) ([boddissattva](https://github.com/boddissattva))
- feat\(\): Run kotlin style scenarios without chutney server [\#24](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/24) ([bessonm](https://github.com/bessonm))

## [0.1.9](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.9) (2021-09-16)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.8...0.1.9)

**Merged pull requests:**

- feat: Add FinalTask [\#23](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/23) ([nbrouand](https://github.com/nbrouand))
- feat: Add component to kotlin feature [\#22](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/22) ([nbrouand](https://github.com/nbrouand))

## [0.1.8](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.8) (2021-06-24)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.7...0.1.8)

**Merged pull requests:**

- chore: Release to maven central [\#18](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/18) ([bessonm](https://github.com/bessonm))
- chore: fix build with ktsrunner does not exist anymore [\#17](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/17) ([nbrouand](https://github.com/nbrouand))
- Complete tasks and functions from Chutney 1.2.20 [\#16](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/16) ([boddissattva](https://github.com/boddissattva))
- fix\(\) : ignore empty parameters of strategies in generated json files [\#12](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/12) ([RedouaeElalami](https://github.com/RedouaeElalami))

## [0.1.7](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.7) (2021-02-11)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.6...0.1.7)

**Merged pull requests:**

- fix\(dsl\): fix empty strategy [\#11](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/11) ([iguissouma](https://github.com/iguissouma))

## [0.1.6](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.6) (2021-02-11)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.5...0.1.6)

**Merged pull requests:**

- feature\(json\): adjust mapper serialization feature [\#9](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/9) ([iguissouma](https://github.com/iguissouma))

## [0.1.5](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.5) (2021-02-10)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.4...0.1.5)

## [0.1.4](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.4) (2021-02-10)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.3...0.1.4)

**Merged pull requests:**

- refactor: Adjust mapper [\#8](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/8) ([bessonm](https://github.com/bessonm))
- chore\(\): Switch from travis to githubaction [\#6](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/6) ([nbrouand](https://github.com/nbrouand))

## [0.1.3](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.3) (2020-11-05)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.2...0.1.3)

**Merged pull requests:**

- feat: Default null value on micrometer tasks [\#5](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/5) ([nbrouand](https://github.com/nbrouand))
- feat: Add micrometer tasks [\#4](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/4) ([nbrouand](https://github.com/nbrouand))

## [0.1.2](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.2) (2020-11-04)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.1...0.1.2)

## [0.1.1](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.1) (2020-09-30)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/0.1.0...0.1.1)

**Merged pull requests:**

- Modiciation needed for CRI team [\#3](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/3) ([nbrouand](https://github.com/nbrouand))
- Update from fork [\#2](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/2) ([nbrouand](https://github.com/nbrouand))
- chore\(\): Add changelog and contributing. Add readme links. [\#1](https://github.com/chutney-testing/chutney-kotlin-dsl/pull/1) ([boddissattva](https://github.com/boddissattva))

## [0.1.0](https://github.com/chutney-testing/chutney-kotlin-dsl/tree/0.1.0) (2020-09-17)

[Full Changelog](https://github.com/chutney-testing/chutney-kotlin-dsl/compare/db2e9c8eb510006971b2d93632f631ca864712e2...0.1.0)



\* *This Changelog was automatically generated by [github_changelog_generator](https://github.com/github-changelog-generator/github-changelog-generator)*
