package ces.neighborhood.blind.app.record.authority;

public record OAuthLoginRes(
        boolean isMember,
        String mbrNickname,
        String mbrProfileImageUrl
) {
}
