# DRAFT 1.0 SPECIFICATION

# Abstract
Black Duck Namespaces provides a framework for describing the common functionality of software packaging and distribution systems. A registry of known namespaces within the framework is also provided as part of the specification.

# Introduction
When describing software components, it is often important to be able to describe the component using the native language of the packaging or distribution systems providing the software. This specification acts as a consolidated reference for describing the native language of the packaging or distribution system and mapping it to a framework of common definitions.

# Namespace Framework
A "namespace" corresponds to a software distribution mechanism and serves as a container for the defintions of the associated rules and conventions. Each namespace MUST have a single canonical identifier. Some namespaces may have additional aliases which may be used in place of the canonical identifier, such aliases may be provided for backwards compatibility with existing systems or simply to address common alternatives.

## Context
Generally an identifier is a relative description of a component, a context is the base that is used to produce an absolute identifier. Typically a context will represent either a base URL or a URL template that can be used in conjunction with a (possibly decomposed) identifier to produce a resolvable URL (generally using HTTP); though some systems may simply have simple tokens as a context, requiring lookup tables or other means to produce an absolute identifier.

## Version
A version number or tag applied to a component at a specific point in it's lifecycle. Some namespaces define specific comparison, equivalence and or canonicalization rules; other namespaces may treat the version as an arbitrary string.

## Version Range
A version range defines a request for one or more versions. Generally build systems will allow a range of versions to be requested and only a single version to be used (potentially scoped). Some namespaces only allow a range consisting of a single version to be specified while others may support complex expressions to define the range of versions.

## Scope
The scope in which an identifier is being considered. Many build systems have separation of "runtime" and "development" scopes under which a component is included; some namespaces may only define a single (therefore optional, or default) scope.

## Identifier
A component identifier. Identifiers are usually associated with the creation of software project and not the usage from another project; for example an identifier would correspond to the GAV defined at the top level of a Maven POM (and *not* the GAV from the dependencies section).

Each namespace will have specific rules about the syntax of its identifiers; namespace managers need only provide minimal decomposition (e.g. extraction of the version portion of the identifier). Individual namespace specific implementations may provide more detailed identifier decomposition.

## Dependency
A dependency to another component. Dependencies are similar to identifiers in that they are used to identify a requirement to another software project, however dependencies may carry additional information used by the namespace specific resolver.

## Locator
A locator represents the actual location of an individual package. Using a locator with the appropriate protocol specific resolver, it is possible to obtain a copy of the package or artifact itself. Most often a locator will be represented as a HTTP(S) URI, however other protocols may be used.

# Registry

## Maven

```ABNF
namespace-token          = "maven"
namespace-context        = repository
namespace-version        = version
namespace-version-range  = version-requirement
namespace-scope          = scope
namespace-identifier     = coordinate

; See [maven-dependency-get]
repository   = ( repo-id "::" [ repo-layout ] "::" repo-url ) / repo-url
repo-id      = token
repo-layout  = "" / "default" / "legacy" / token
repo-url     = URI-reference

; Only a sort order is defined, see [maven-version-order]
version      = token

; See [maven-version-requirement]
version-requirement      = version-rqmt-spec / ( version-rqmt-spec "," version-rqmt-spec )
version-rqmt-spec        = version-rqmt-soft / version-rqmt-hard / version-rqmt-range
version-rqmt-soft        = version
version-rqmt-hard        = "[" version "]"
version-rqmt-range       = ( "[" / "(" ) version-rqmt-range-spec ( ")" / "]" )
version-rqmt-range-spec  = ( "," version ) / ( version "," version ) / ( version ",")

; See [maven-dependencies]
scope        = "compile" / "provided" / "runtime" / "test" / "system"

; See [maven-coordinates]
coordinate   = group-id ":" artifact-id [ ":" packaging [ ":" classifier ] ] ":" version
group-id     = token
artifact-id  = token
packaging    = "pom" / "jar" / "maven-plugin" / "ejb" / "war" / "ear" / "rar" / "par" / token
classifier   = token

; There is no standard encoded form for a dependency, see [maven-dependencies]
type         = "jar" / token
system-path  = local-path
optional     = BOOLEAN
```

## Reserved Namespace Tokens
The following list of namespace tokens are reserved for future definition by this specification.

* `alpine`
* `anaconda`
* `android`
* `apache`
* `apache_software`
* `arch`
* `atom`
* `automotive_linux`
* `bdhub`
* `bdsuite`
* `bitbucket`
* `bower`
* `brew`
* `buildroot`
* `cargo`
* `carthage`
* `centos`
* `chef`
* `clearlinux`
* `clojars`
* `cocoapods`
* `codeplex`
* `codeplex_group`
* `composer`
* `conan`
* `coreos`
* `cpan`
* `cpe`
* `cran`
* `crystal`
* `ctan`
* `deb`
* `debian`
* `docker`
* `dpkg`
* `drupal`
* `dtype`
* `dub`
* `ebuild`
* `eclipse`
* `efisbot`
* `elm`
* `fedora`
* `freedesktop_org`
* `gem`
* `generic`
* `gitcafe`
* `gitea`
* `github`
* `github_gist`
* `gitlab`
* `gitorious`
* `gnu`
* `goget`
* `golang`
* `googlecode`
* `guix`
* `hackage`
* `haxe`
* `hex`
* `java_net`
* `julia`
* `kde_org`
* `launchpad`
* `long_tail`
* `lua`
* `melpa`
* `meteor`
* `nim`
* `nix`
* `npm`
* `npmjs`
* `nuget`
* `opam`
* `opensuse`
* `openwrt`
* `osgi`
* `p2`
* `packagist`
* `pear`
* `pecl`
* `perl6`
* `platformio`
* `protecode_sc`
* `pub`
* `puppet`
* `pypi`
* `redhat`
* `rpm`
* `rubyforge`
* `rubygems`
* `runtime`
* `sourceforge`
* `sourceforge_jp`
* `sublime`
* `swift`
* `tianocore`
* `ubuntu`
* `vim`
* `wordpress`
* `yocto`

# Appendix A: Registry Template
Each registration uses the Augmented Backus-Naur Form (ABNF) notation of [RFC5234] to define the rules for each of the namespace framework types. By convention, only rules starting with `namespace-` are exported. Supporting rules names should use terminology native to the namespace being defined and are considered unique to each registration. All of the [RFC5234] Appendix B.1 core rules are included by reference, as are the core BDNS Appendix A.1 rules.

A `namespace-token` rule is used to define an identifying token for the namespace. Namespace tokens MUST conform to the `token` rule of the "Core Rules" Appendix. Namespace tokens MUST NOT be ambiguious across registrations. If the namespace token is defined using an alternative list, the first token MUST be canonical.

Each registration should start with this template:

```TEXT
    ## {Namespace Name}

    ```ABNF
    namespace-token          = {namespace token rule}
    namespace-context        = {context rule}
    namespace-version        = {version rule}
    namespace-version-range  = {version range rule}
    namespace-scope          = {scope rule}
    namespace-identifier     = {identifier rule}
    namespace-dependency     = {dependency rule}
    namespace-locator        = {locator rule}
    
    {additional rules as necessary}
    ```
```

## Appendix A.1: Core Rules

```ABNF
BOOLEAN        = "true" / "false"
token          = 1*( ALPHA / DIGIT / "." / "-" / "_" / "+" ) ; TODO Anything else?
URI-reference  = <see [RFC3986], Section 4.1>
local-path     = <see [RFC8089], Section 2>
```

# Appendix B: Collected Namespace Tokens

* `maven`


[RFC3986]: https://tools.ietf.org/html/rfc3986 "Uniform Resource Identifier (URI): Generic Syntax"

[RFC5234]: https://tools.ietf.org/html/rfc5234 "Augmented BNF for Syntax Specifications: ABNF"

[RFC8089]: https://tools.ietf.org/html/rfc8089 "The ``file'' URI Scheme"

[maven-dependency-get]: https://maven.apache.org/plugins/maven-dependency-plugin/get-mojo.html#remoteRepositories "Apache Maven Dependency Plugin / dependency:get"

[maven-version-requirement]: https://maven.apache.org/pom.html#Dependency_Version_Requirement_Specification "POM Reference / Dependency Version Requirement Specification"

[maven-version-order]: https://maven.apache.org/pom.html#Version_Order_Specification "POM Reference / Version Order Specification"

[maven-coordinates]: https://maven.apache.org/pom.html#Maven_Coordinates "POM Reference / Maven Coordinates"

[maven-dependencies]: https://maven.apache.org/pom.html#Dependencies "POM Reference / Dependencies"
